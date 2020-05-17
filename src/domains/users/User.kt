package com.comprendre.domains.users

data class User(val id: Long, val familyName: String, val givenName: String) {
    val fullName = "$familyName $givenName"
}

data class Users(val users: List<User>)
