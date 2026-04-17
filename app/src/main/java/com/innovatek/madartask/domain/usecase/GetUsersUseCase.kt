package com.innovatek.madartask.domain.usecase

import androidx.paging.PagingData
import com.innovatek.madartask.domain.model.SortOrder
import com.innovatek.madartask.domain.model.User
import com.innovatek.madartask.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(sortOrder: SortOrder): Flow<PagingData<User>> =
        repository.getUsers(sortOrder)
}
