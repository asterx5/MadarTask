package com.madarsoft.madartask.domain.repository

import androidx.paging.PagingData
import com.madarsoft.madartask.domain.model.SortOrder
import com.madarsoft.madartask.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(id: Int)
    suspend fun clearAll()
    fun getUsers(sortOrder: SortOrder): Flow<PagingData<User>>
}
