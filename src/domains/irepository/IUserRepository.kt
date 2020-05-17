package com.comprendre.domains.irepository

import com.comprendre.domains.users.User

interface IUserRepository {
    fun findById(userId: Long): User?
}
