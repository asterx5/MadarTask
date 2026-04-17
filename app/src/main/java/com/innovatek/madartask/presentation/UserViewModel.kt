package com.innovatek.madartask.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.innovatek.madartask.domain.model.Gender
import com.innovatek.madartask.domain.model.SortOrder
import com.innovatek.madartask.domain.model.User
import com.innovatek.madartask.domain.usecase.AddUserUseCase
import com.innovatek.madartask.domain.usecase.ClearAllUsersUseCase
import com.innovatek.madartask.domain.usecase.DeleteUserUseCase
import com.innovatek.madartask.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class UserViewModel(
    private val addUserUseCase: AddUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val clearAllUsersUseCase: ClearAllUsersUseCase,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    val sortOrder: MutableStateFlow<SortOrder> = MutableStateFlow(SortOrder.ID_DESC)

    val users: Flow<PagingData<User>> = sortOrder
        .flatMapLatest { order -> getUsersUseCase(order) }
        .cachedIn(viewModelScope)

    fun onSortOrderSelected(order: SortOrder) { sortOrder.value = order }

    fun addUser(name: String, age: Int, jobTitle: String, gender: Gender) {
        viewModelScope.launch {
            addUserUseCase(User(name = name, age = age, jobTitle = jobTitle, gender = gender))
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch { deleteUserUseCase(id) }
    }

    fun clearAll() {
        viewModelScope.launch { clearAllUsersUseCase() }
    }

    fun seedDatabase() {
        val firstNames = listOf(
            "Ahmed", "Sara", "Mohammed", "Fatima", "Omar", "Layla", "Ali", "Nour", "Khalid", "Rana",
            "Yusuf", "Hana", "Ibrahim", "Mariam", "Tariq", "Dina", "Hassan", "Rania", "Samira", "Karim",
            "James", "Emily", "Liam", "Olivia", "Noah", "Ava", "William", "Sophia", "Benjamin", "Isabella",
            "Lucas", "Mia", "Henry", "Charlotte", "Alexander", "Amelia", "Mason", "Harper", "Ethan", "Evelyn",
            "Yuki", "Haruto", "Sakura", "Kenji", "Aiko", "Ryo", "Hana", "Takeshi", "Mei", "Daiki",
            "Diego", "Valentina", "Santiago", "Camila", "Mateo", "Lucia", "Sebastian", "Sofia", "Andres", "Isabela",
            "Leon", "Emma", "Paul", "Anna", "Felix", "Laura", "Max", "Julia", "Lukas", "Marie",
            "Arjun", "Priya", "Rahul", "Anjali", "Vikram", "Deepa", "Rohan", "Neha", "Amit", "Pooja"
        )
        val lastNames = listOf(
            "Al-Rashid", "Hassan", "Ibrahim", "Mahmoud", "Abdullah", "Salem", "Nasser", "Khalil", "Mansour", "Yousef",
            "Farouk", "Aziz", "Hamdan", "Suleiman", "Qasim", "Al-Farsi", "Karimi", "Nassar", "Haddad", "Bishara",
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Wilson", "Taylor",
            "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin", "Thompson", "Martinez", "Robinson", "Clark",
            "Tanaka", "Yamamoto", "Suzuki", "Watanabe", "Ito", "Kobayashi", "Kato", "Nakamura", "Sato", "Hayashi",
            "Rodriguez", "Gonzalez", "Lopez", "Hernandez", "Perez", "Torres", "Flores", "Rivera", "Ramirez", "Cruz",
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
            "Sharma", "Verma", "Patel", "Singh", "Kumar", "Mehta", "Joshi", "Gupta", "Reddy", "Nair"
        )
        val jobTitles = listOf(
            "Software Engineer", "Senior Software Engineer", "Staff Engineer", "Principal Engineer",
            "Product Manager", "Senior Product Manager", "Director of Product",
            "UI Designer", "UX Designer", "Product Designer", "Design Lead",
            "Data Analyst", "Senior Data Analyst", "Data Scientist", "ML Engineer",
            "DevOps Engineer", "Platform Engineer", "Site Reliability Engineer", "Cloud Architect",
            "QA Engineer", "SDET", "Test Lead", "Quality Manager",
            "Project Manager", "Scrum Master", "Agile Coach", "Program Manager",
            "Backend Developer", "Frontend Developer", "Full Stack Developer", "Mobile Developer",
            "Business Analyst", "Systems Analyst", "Solutions Architect", "Enterprise Architect",
            "CTO", "VP of Engineering", "Engineering Manager", "Tech Lead",
            "Marketing Manager", "Growth Manager", "Content Strategist", "SEO Specialist",
            "HR Specialist", "HR Manager", "Talent Acquisition", "People Operations",
            "Financial Analyst", "Accountant", "CFO", "Controller",
            "Sales Engineer", "Account Executive", "Customer Success Manager", "Support Engineer",
            "Security Engineer", "Penetration Tester", "CISO", "Compliance Officer"
        )
        viewModelScope.launch {
            repeat(1000) {
                addUserUseCase(
                    User(
                        name = "${firstNames.random()} ${lastNames.random()}",
                        age = (18..65).random(),
                        jobTitle = jobTitles.random(),
                        gender = listOf(Gender.MALE, Gender.FEMALE).random()
                    )
                )
            }
        }
    }
}
