package com.madarsoft.madartask.di

import androidx.room.Room
import com.madarsoft.madartask.data.local.datasource.UserLocalDataSource
import com.madarsoft.madartask.data.local.datasource.UserLocalDataSourceImpl
import com.madarsoft.madartask.data.local.db.AppDatabase
import com.madarsoft.madartask.data.repository.UserRepositoryImpl
import com.madarsoft.madartask.domain.repository.UserRepository
import com.madarsoft.madartask.domain.usecase.AddUserUseCase
import com.madarsoft.madartask.domain.usecase.ClearAllUsersUseCase
import com.madarsoft.madartask.domain.usecase.DeleteUserUseCase
import com.madarsoft.madartask.domain.usecase.GetUsersUseCase
import com.madarsoft.madartask.presentation.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }
    single { get<AppDatabase>().userDao() }

    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }

    single<UserRepository> { UserRepositoryImpl(get()) }

    factory { AddUserUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
    factory { ClearAllUsersUseCase(get()) }
    factory { GetUsersUseCase(get()) }

    viewModel { UserViewModel(get(), get(), get(), get()) }
}
