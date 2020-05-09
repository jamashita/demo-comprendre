package com.comprendre

import com.comprendre.repository.dao.Memo
import com.comprendre.repository.dao.MemoMemo
import com.comprendre.repository.dao.Memos
import io.ktor.response.*
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(AutoHeadResponse)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ConditionalHeaders)

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

//    if (environment.config.propertyOrNull("ktor.deployment.environment") == null) {
//        print("load this");
//    }
    Database.connect(
        url = environment.config.property("app.database.url").getString(),
        user = environment.config.property("app.database.user").getString(),
        password = environment.config.property("app.database.password").getString(),
        driver = "org.postgresql.Driver"
    )

    routing {
        get("/") {
            call.respondText("HELLO WORLD????", contentType = ContentType.Text.Plain)
        }

        get("/echo/{echo}") {
            val s: String? = call.parameters["echo"]
            call.respondText(s ?: "EMPTY", contentType = ContentType.Text.Plain)
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }
        }

        memos()

        webSocket("/myws/echo") {
            send(Frame.Text("Hi from server"))
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("Client said: " + frame.readText()))
                }
            }
        }
    }
}

fun Routing.memos() = route("memos") {
    get {
        val memos = transaction {
            Memo.all()
        }

        call.respond(memos)
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
        val memo = transaction {
            Memo.findById(id)
        } ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(memo)
    }

    post {
        val memomemo = call.receive<MemoMemo>()
        val id = transaction {
            Memos.insertAndGetId {
                it[subject] = memomemo.subject
            }
        }

        call.respond(
            HttpStatusCode.Created,
            mapOf(
                "memo_id" to id.value
            )
        )
    }

    put("/{id}") {
        val id = call.parameters["id"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest)
        val memomemo = call.receive<MemoMemo>()
        val affected = transaction {
            Memos.update({Memos.id eq id}) {
                it[subject] = memomemo.subject
            }
        }

        if (affected == 0) {
            return@put call.respond(HttpStatusCode.NotFound)
        }
        call.respond(HttpStatusCode.OK)
    }

    delete("/{id}") {
        val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val affected = transaction {
            Memos.deleteWhere { Memos.id eq id }
        }

        if (affected == 0) {
            return@delete call.respond(HttpStatusCode.NotFound)
        }
        call.respond(HttpStatusCode.NoContent)
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

