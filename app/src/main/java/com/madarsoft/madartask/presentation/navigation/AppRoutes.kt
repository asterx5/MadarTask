package com.madarsoft.madartask.presentation.navigation

sealed class Screen {
    data object DisplayUsers : Screen()
    data object AddUser : Screen()
}
