package com.madarsoft.madartask

import android.content.Context
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.madarsoft.madartask.data.local.db.AppDatabase
import com.madarsoft.madartask.data.local.db.UserDao
import com.madarsoft.madartask.data.local.entity.UserEntity
import com.madarsoft.madartask.domain.model.Gender
import com.madarsoft.madartask.domain.model.SortOrder
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoPagingTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        val ctx: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun insertUsers(count: Int) {
        repeat(count) { i ->
            dao.insertUser(
                UserEntity(
                    name     = "User${i.toString().padStart(4, '0')}",
                    age      = 20 + (i % 50),
                    jobTitle = "Title${i % 10}",
                    gender   = if (i % 2 == 0) Gender.MALE else Gender.FEMALE
                )
            )
        }
    }

    private fun pagingConfig(pageSize: Int = 20) =
        PagingConfig(pageSize = pageSize, initialLoadSize = pageSize, enablePlaceholders = false)

    @Test
    fun firstPage_returns_exactly_20_items() = runTest {
        insertUsers(100)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.ID_ASC)))

        val page = pager.refresh() as PagingSource.LoadResult.Page
        assertEquals(20, page.data.size)
    }

    @Test
    fun second_page_appends_next_20_items() = runTest {
        insertUsers(100)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.ID_ASC)))

        pager.refresh()
        val page2 = pager.append() as PagingSource.LoadResult.Page
        assertEquals(20, page2.data.size)
    }

    @Test
    fun all_pages_cover_all_inserted_items() = runTest {
        val total = 55
        insertUsers(total)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.ID_ASC)))

        val allItems = mutableListOf<UserEntity>()
        var page = pager.refresh() as PagingSource.LoadResult.Page
        allItems += page.data
        while (page.nextKey != null) {
            page = pager.append() as PagingSource.LoadResult.Page
            allItems += page.data
        }
        assertEquals(total, allItems.size)
    }

    @Test
    fun last_page_has_remainder_items() = runTest {
        insertUsers(45)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.ID_ASC)))

        pager.refresh()       // page 1: 20 items
        pager.append()        // page 2: 20 items
        val last = pager.append() as PagingSource.LoadResult.Page
        assertEquals(5, last.data.size)
    }

    @Test
    fun empty_table_returns_empty_page() = runTest {
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.ID_ASC)))

        val page = pager.refresh() as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
    }

    @Test
    fun name_asc_sort_returns_alphabetical_order() = runTest {
        insertUsers(40)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.NAME_ASC)))

        val page = pager.refresh() as PagingSource.LoadResult.Page
        val names = page.data.map { it.name }
        assertEquals(names.sorted(), names)
    }

    @Test
    fun name_desc_sort_returns_reverse_alphabetical_order() = runTest {
        insertUsers(40)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.NAME_DESC)))

        val page = pager.refresh() as PagingSource.LoadResult.Page
        val names = page.data.map { it.name }
        assertEquals(names.sortedDescending(), names)
    }

    @Test
    fun age_asc_sort_first_page_is_non_decreasing() = runTest {
        insertUsers(40)
        val pager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.AGE_ASC)))

        val page = pager.refresh() as PagingSource.LoadResult.Page
        val ages = page.data.map { it.age }
        assertEquals(ages.sorted(), ages)
    }

    @Test
    fun changing_sort_produces_independent_pager_with_inverted_boundaries() = runTest {
        insertUsers(40)

        val ascPager  = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.NAME_ASC)))
        val descPager = TestPager(pagingConfig(), dao.getUsersRaw(UserDao.buildSortQuery(SortOrder.NAME_DESC)))

        val ascPage  = ascPager.refresh()  as PagingSource.LoadResult.Page
        val descPage = descPager.refresh() as PagingSource.LoadResult.Page

        // Each pager is internally ordered correctly
        assertTrue(ascPage.data.first().name  < ascPage.data.last().name)
        assertTrue(descPage.data.first().name > descPage.data.last().name)
        // ASC starts at the lowest name, DESC at the highest — they read from opposite ends
        assertTrue(ascPage.data.first().name < descPage.data.first().name)
    }
}
