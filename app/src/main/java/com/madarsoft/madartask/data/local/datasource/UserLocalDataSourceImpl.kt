package com.madarsoft.madartask.data.local.datasource

import androidx.paging.PagingSource
import com.madarsoft.madartask.data.local.db.UserDao
import com.madarsoft.madartask.data.local.entity.UserEntity
import com.madarsoft.madartask.domain.model.SortOrder

class UserLocalDataSourceImpl(private val userDao: UserDao) : UserLocalDataSource {
    override suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    override suspend fun deleteUser(id: Int) = userDao.deleteUser(id)
    override suspend fun clearAll() = userDao.clearAll()
    override fun getUsers(sortOrder: SortOrder): PagingSource<Int, UserEntity> =
        userDao.getUsersRaw(UserDao.buildSortQuery(sortOrder))
}
