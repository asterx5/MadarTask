package com.madarsoft.madartask.data.local.datasource

import androidx.paging.PagingSource
import com.madarsoft.madartask.data.local.entity.UserEntity
import com.madarsoft.madartask.domain.model.SortOrder

interface UserLocalDataSource {
    suspend fun insertUser(user: UserEntity)
    suspend fun deleteUser(id: Int)
    suspend fun clearAll()
    fun getUsers(sortOrder: SortOrder): PagingSource<Int, UserEntity>
}
