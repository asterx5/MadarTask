package com.innovatek.madartask.presentation.navigation

sealed class Screen {
    data object DisplayUsers : Screen()
    data object AddUser : Screen()
}
