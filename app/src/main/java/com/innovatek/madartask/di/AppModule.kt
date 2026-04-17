package com.innovatek.madartask.di

import androidx.room.Room
import com.innovatek.madartask.data.local.datasource.UserLocalDataSource
import com.innovatek.madartask.data.local.datasource.UserLocalDataSourceImpl
import com.innovatek.madartask.data.local.db.AppDatabase
import com.innovatek.madartask.data.repository.UserRepositoryImpl
import com.innovatek.madartask.domain.repository.UserRepository
import com.innovatek.madartask.domain.usecase.AddUserUseCase
import com.innovatek.madartask.domain.usecase.ClearAllUsersUseCase
import com.innovatek.madartask.domain.usecase.DeleteUserUseCase
import com.innovatek.madartask.domain.usecase.GetUsersUseCase
import com.innovatek.madartask.presentation.UserViewModel
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
