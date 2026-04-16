package com.innovatek.madartask.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.innovatek.madartask.domain.model.Gender
import com.innovatek.madartask.domain.model.User
import com.innovatek.madartask.domain.usecase.AddUserUseCase
import com.innovatek.madartask.domain.usecase.DeleteUserUseCase
import com.innovatek.madartask.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserViewModel(
    private val addUserUseCase: AddUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val users: Flow<PagingData<User>> = getUsersUseCase().cachedIn(viewModelScope)

    fun addUser(name: String, age: Int, jobTitle: String, gender: Gender) {
        viewModelScope.launch {
            addUserUseCase(User(name = name, age = age, jobTitle = jobTitle, gender = gender))
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch { deleteUserUseCase(id) }
    }

    fun seedDatabase() {
        val firstNames = listOf("Ahmed", "Sara", "Mohammed", "Fatima", "Omar", "Layla", "Ali", "Nour", "Khalid", "Rana", "Yusuf", "Hana", "Ibrahim", "Mariam", "Tariq", "Dina", "Hassan", "Rania", "Samira", "Karim")
        val lastNames = listOf("Al-Rashid", "Hassan", "Ibrahim", "Mahmoud", "Abdullah", "Salem", "Nasser", "Khalil", "Mansour", "Yousef", "Farouk", "Aziz", "Hamdan", "Suleiman", "Qasim")
        val jobTitles = listOf("Software Engineer", "Product Manager", "UI Designer", "Data Analyst", "DevOps Engineer", "QA Engineer", "Project Manager", "Backend Developer", "Frontend Developer", "Business Analyst", "Scrum Master", "CTO", "Marketing Manager", "HR Specialist", "System Architect")
        viewModelScope.launch {
            repeat(1000) {
                addUserUseCase(User(name = "${firstNames.random()} ${lastNames.random()}", age = (18..60).random(), jobTitle = jobTitles.random(), gender = Gender.entries.random()))
            }
        }
    }
}
