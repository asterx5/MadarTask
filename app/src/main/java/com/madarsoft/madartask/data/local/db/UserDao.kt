package com.madarsoft.madartask.data.local.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.madarsoft.madartask.data.local.entity.UserEntity
import com.madarsoft.madartask.domain.model.SortOrder

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int)

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @RawQuery(observedEntities = [UserEntity::class])
    fun getUsersRaw(query: SupportSQLiteQuery): PagingSource<Int, UserEntity>

    companion object {
        fun buildSortQuery(sortOrder: SortOrder): SupportSQLiteQuery {
            val (column, direction) = when (sortOrder) {
                SortOrder.NAME_ASC       -> "name"     to "ASC"
                SortOrder.NAME_DESC      -> "name"     to "DESC"
                SortOrder.AGE_ASC        -> "age"      to "ASC"
                SortOrder.AGE_DESC       -> "age"      to "DESC"
                SortOrder.JOB_TITLE_ASC  -> "jobTitle" to "ASC"
                SortOrder.JOB_TITLE_DESC -> "jobTitle" to "DESC"
                SortOrder.ID_DESC        -> "id"       to "DESC"
                SortOrder.ID_ASC         -> "id"       to "ASC"
            }
            return SimpleSQLiteQuery("SELECT * FROM users ORDER BY $column $direction")
        }
    }
}
