package com.comprendre

import com.comprendre.controllers.UserController
import com.comprendre.controllers.icontrollers.IUserController
import com.comprendre.databases.repository.UserRepository
import com.comprendre.domains.irepository.IUserRepository
import com.comprendre.usecases.UserService
import com.comprendre.usecases.iservice.IUserService
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

val koinModules = module(createdAtStart = true) {
    singleBy<IUserController, UserController>()
    singleBy<IUserService, UserService>()
    singleBy<IUserRepository, UserRepository>()
}
