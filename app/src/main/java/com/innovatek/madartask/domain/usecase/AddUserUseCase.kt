package com.innovatek.madartask.domain.usecase

import com.innovatek.madartask.domain.model.User
import com.innovatek.madartask.domain.repository.UserRepository

class AddUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.addUser(user)
}
