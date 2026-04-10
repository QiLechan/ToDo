package org.yuezhikong.todo.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ToDo(val id: String)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val HomeStack = remember { mutableStateListOf<ToDo>(ToDo("1")) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(240.dp),
                    drawerShape = RectangleShape,
                ) {
                    Text("ToDo", modifier = Modifier.padding(16.dp))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(text = "今天") },
                        selected = HomeStack.lastOrNull()?.id == "1",
                        onClick = {
                            HomeStack.removeLastOrNull()
                            HomeStack.add(ToDo("1"))
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "明天") },
                        selected = HomeStack.lastOrNull()?.id == "2",
                        onClick = {
                            HomeStack.removeLastOrNull()
                            HomeStack.add(ToDo("2"))
                            scope.launch { drawerState.close() }
                        }
                    )
                }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("今天") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单")
                        }
                    }
                )
            }
        )
        { paddingValues ->
            NavDisplay(
                backStack = HomeStack,
                onBack = { HomeStack.removeLastOrNull() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                entryProvider = { key ->
                    when (key.id) {
                        "1" -> NavEntry(key) {
                            Today()
                        }

                        "2" -> NavEntry(key) {
                            Tomorrow()
                        }
                        else -> error("Unknown key: ${key.id}")
                    }
                }
            )
        }
    }
}

@Composable
fun Today() {
    Text("Today")
}

@Composable
fun Tomorrow() {
    Text("Tomorrow")
}