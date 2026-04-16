package com.innovatek.madartask.domain.usecase

import com.innovatek.madartask.domain.repository.UserRepository

class DeleteUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteUser(id)
}
