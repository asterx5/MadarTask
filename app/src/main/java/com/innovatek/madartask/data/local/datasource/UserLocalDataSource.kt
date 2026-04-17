package com.innovatek.madartask.data.local.datasource

import androidx.paging.PagingSource
import com.innovatek.madartask.data.local.entity.UserEntity
import com.innovatek.madartask.domain.model.SortOrder

interface UserLocalDataSource {
    suspend fun insertUser(user: UserEntity)
    suspend fun deleteUser(id: Int)
    suspend fun clearAll()
    fun getUsers(sortOrder: SortOrder): PagingSource<Int, UserEntity>
}
