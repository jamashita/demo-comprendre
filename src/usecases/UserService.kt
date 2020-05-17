package com.comprendre.usecases

import com.comprendre.domains.irepository.IUserRepository
import com.comprendre.domains.users.User
import com.comprendre.usecases.iservice.IUserService

class UserService(private val userRepository: IUserRepository) : IUserService {
    override fun findById(userId: Long): User {
        return userRepository.findById(userId) ?: throw IllegalStateException("No User Found for Given Id")
    }
}
