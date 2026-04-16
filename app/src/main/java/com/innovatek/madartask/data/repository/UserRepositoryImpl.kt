package com.innovatek.madartask.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.innovatek.madartask.data.local.datasource.UserLocalDataSource
import com.innovatek.madartask.data.local.entity.toDomain
import com.innovatek.madartask.data.local.entity.toEntity
import com.innovatek.madartask.domain.model.User
import com.innovatek.madartask.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val localDataSource: UserLocalDataSource
) : UserRepository {

    override suspend fun addUser(user: User) {
        localDataSource.insertUser(user.toEntity())
    }

    override suspend fun deleteUser(id: Int) {
        localDataSource.deleteUser(id)
    }

    override fun getUsers(): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false)
        ) {
            localDataSource.getUsers()
        }.flow.map { pagingData -> pagingData.map { it.toDomain() } }
}
