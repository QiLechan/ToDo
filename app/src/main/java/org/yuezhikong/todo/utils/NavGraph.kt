package org.yuezhikong.todo.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import org.yuezhikong.todo.ui.home.HomeScreen

data object Home
data class Product(val id: String)

@Composable
fun NavGraph(){
    val backStack = remember { mutableStateListOf<Any>(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    HomeScreen()
                }
                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        }
    )
}