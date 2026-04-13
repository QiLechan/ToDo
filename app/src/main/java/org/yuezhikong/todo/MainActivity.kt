package org.yuezhikong.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import org.yuezhikong.todo.ui.home.CalendarScreen
import org.yuezhikong.todo.ui.home.HomeScreen
import org.yuezhikong.todo.ui.home.UserScreen
import org.yuezhikong.todo.ui.theme.ToDoTheme

data object Home
data object Calendar
data object User

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoTheme {
                ToDo()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDo() {
    val backStack = remember { mutableStateListOf<Any>(Home) }
    val showBottomBar = backStack.lastOrNull() in setOf(Home, Calendar, User)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { BottomBar(showBottomBar, backStack.last().toString(), backStack) },
    ) { paddingValues ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            entryProvider = { key ->
                when (key) {
                    is Home -> NavEntry(key) {
                        HomeScreen()
                    }
                    is Calendar -> NavEntry(key) {
                        CalendarScreen()
                    }
                    is User -> NavEntry(key) {
                        UserScreen()
                    }
                    else -> NavEntry(Unit) { Text("Unknown route") }
                }
            }
        )
    }
}

@Composable
fun BottomBar(showBottomBar: Boolean, currentRoute: String, backStack: SnapshotStateList<Any>) {
    if (showBottomBar) {
        NavigationBar {
            NavigationBarItem(
                selected = currentRoute == "Home",
                onClick = {
                    backStack.removeLastOrNull()
                    backStack.add(Home)
                },
                icon = { Icon(Icons.Filled.Home, null) },
                label = { Text("首页") }
            )
            NavigationBarItem(
                selected = currentRoute == "Calendar",
                onClick = {
                    backStack.removeLastOrNull()
                    backStack.add(Calendar)
                },
                icon = { Icon(Icons.Filled.DateRange, null) },
                label = { Text("日历") }
            )
            NavigationBarItem(
                selected = currentRoute == "User",
                onClick = {
                    backStack.removeLastOrNull()
                    backStack.add(User)
                },
                icon = { Icon(Icons.Filled.Person, null) },
                label = { Text("我的") }
            )
        }
    }
}