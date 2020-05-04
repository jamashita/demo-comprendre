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
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
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

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        install(StatusPages) {
            exception<AuthenticationException> { cause ->
                call.respond(HttpStatusCode.Unauthorized)
            }
            exception<AuthorizationException> { cause ->
                call.respond(HttpStatusCode.Forbidden)
            }

        }

        get("/memos/{id}") {
            val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
            val memoEntity =
                transaction { Memo.findById(id) }
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(
                mapOf(
                    "memo_id" to memoEntity.id.value,
                    "subject" to memoEntity.subject
                )
            )
        }

        post("/memos") {
            val parameter = call.receive<MemoMemo>()
            val id = transaction {
                Memos.insertAndGetId {
                    it[subject] = parameter.subject
                }
            }

            call.respond(
                HttpStatusCode.Created,
                mapOf(
                    "memo_id" to id.value
                )
            )
        }

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

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

