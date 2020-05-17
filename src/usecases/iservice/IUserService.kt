package com.comprendre.usecases.iservice

import com.comprendre.domains.users.User

interface IUserService {
    fun findById(userId: Long): User
}
