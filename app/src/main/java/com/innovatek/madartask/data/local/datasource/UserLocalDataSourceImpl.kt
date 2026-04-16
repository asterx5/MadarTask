package com.innovatek.madartask.data.local.datasource

import androidx.paging.PagingSource
import com.innovatek.madartask.data.local.db.UserDao
import com.innovatek.madartask.data.local.entity.UserEntity

class UserLocalDataSourceImpl(private val userDao: UserDao) : UserLocalDataSource {
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    override suspend fun deleteUser(id: Int) = userDao.deleteUser(id)
    override fun getUsers(): PagingSource<Int, UserEntity> = userDao.getUsers()
}
