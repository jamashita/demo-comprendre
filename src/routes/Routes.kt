package com.comprendre.routes

import com.comprendre.infrastructures.routes.memos
import com.comprendre.infrastructures.routes.users
import io.ktor.routing.Routing

fun Routing.routes() {
    memos()
    // ユーザ機能
    users()
}
