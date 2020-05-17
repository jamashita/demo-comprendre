package com.comprendre.databases.repository

import com.comprendre.domains.irepository.IUserRepository
import com.comprendre.domains.users.User

class UserRepository : IUserRepository {
    override fun findById(userId: Long): User? {
        // TODO DBからのデータ形式を、domainsに詰め替え。Service層にinject
        return User(1, "Test", "Taro")
    }
}
