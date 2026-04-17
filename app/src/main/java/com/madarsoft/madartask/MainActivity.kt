package com.madarsoft.madartask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.madarsoft.madartask.presentation.UserViewModel
import com.madarsoft.madartask.presentation.add.AddUserScreen
import com.madarsoft.madartask.presentation.display.UsersScreen
import com.madarsoft.madartask.presentation.navigation.Screen
import com.madarsoft.madartask.ui.theme.MadarTaskTheme
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MadarTaskTheme {
                val backStack = remember { mutableStateListOf<Any>(Screen.DisplayUsers) }
                val viewModel = koinViewModel<UserViewModel>()
                //LaunchedEffect(Unit) { viewModel.seedDatabase() }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<Screen.DisplayUsers> {
                            UsersScreen(
                                viewModel = viewModel,
                                onNavigateToAdd = dropUnlessResumed {
                                    backStack.add(Screen.AddUser)
                                }
                            )
                        }
                        entry<Screen.AddUser> {
                            AddUserScreen(
                                viewModel = viewModel,
                                onBack = dropUnlessResumed {
                                    backStack.removeLastOrNull()
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
