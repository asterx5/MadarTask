package com.innovatek.madartask.data.local.datasource

import androidx.paging.PagingSource
import com.innovatek.madartask.data.local.entity.UserEntity

interface UserLocalDataSource {
    suspend fun insertUser(user: UserEntity)
    suspend fun deleteUser(id: Int)
    fun getUsers(): PagingSource<Int, UserEntity>
}
