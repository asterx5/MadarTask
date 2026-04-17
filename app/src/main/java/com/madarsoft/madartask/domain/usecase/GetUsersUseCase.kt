package com.madarsoft.madartask.domain.usecase

import androidx.paging.PagingData
import com.madarsoft.madartask.domain.model.SortOrder
import com.madarsoft.madartask.domain.model.User
import com.madarsoft.madartask.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(sortOrder: SortOrder): Flow<PagingData<User>> =
        repository.getUsers(sortOrder)
}
