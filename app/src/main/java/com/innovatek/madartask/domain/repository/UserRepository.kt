package com.innovatek.madartask.domain.repository

import androidx.paging.PagingData
import com.innovatek.madartask.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(user: User)
    suspend fun deleteUser(id: Int)
    fun getUsers(): Flow<PagingData<User>>
}
