package com.comprendre.controllers.icontrollers

// IN
data class UserId(var id: Long)

// OUT
data class UserResponse(var userId: Long, var familyName: String, var givenName: String)

interface IUserController {
    fun getUser(userId: UserId): UserResponse
}
