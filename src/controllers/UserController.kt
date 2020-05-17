package com.comprendre.controllers

import com.comprendre.domains.users.User
import com.comprendre.controllers.icontrollers.IUserController
import com.comprendre.controllers.icontrollers.UserId
import com.comprendre.controllers.icontrollers.UserResponse
import com.comprendre.usecases.iservice.IUserService

class UserController(private val userService: IUserService) :
    IUserController {
    override fun getUser(userId: UserId): UserResponse {
        return userService.findById(userId.id).toResponse()
    }
}

private fun User.toResponse() =
    UserResponse(id, familyName, givenName)
