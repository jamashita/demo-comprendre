package com.comprendre.infrastructures.routes

import com.comprendre.repository.dao.Memo
import com.comprendre.repository.dao.MemoMemo
import com.comprendre.repository.dao.Memos
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.delete
import io.ktor.routing.route
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
            Memos.update({ Memos.id eq id}) {
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
