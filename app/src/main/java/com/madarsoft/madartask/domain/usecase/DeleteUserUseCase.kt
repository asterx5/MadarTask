package com.madarsoft.madartask.domain.usecase

import com.madarsoft.madartask.domain.repository.UserRepository

class DeleteUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: Int) = repository.deleteUser(id)
}
