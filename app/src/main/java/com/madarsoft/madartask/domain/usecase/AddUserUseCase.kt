package com.madarsoft.madartask.domain.usecase

import com.madarsoft.madartask.domain.model.User
import com.madarsoft.madartask.domain.repository.UserRepository

class AddUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.addUser(user)
}
