package org.yuezhikong.todo.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.yuezhikong.todo.Add
import org.yuezhikong.todo.database.AppDatabase.Companion.getDatabase
import org.yuezhikong.todo.database.Schedule
import org.yuezhikong.todo.ui.widget.ScheduleWidget
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ToDo(val id: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(backStack: SnapshotStateList<Any>) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val HomeStack = remember { mutableStateListOf<ToDo>(ToDo("1")) }
    val context = LocalContext.current
    val db = remember { getDatabase(context) }
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    var todayList by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var tomorrowList by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var schedule by remember { mutableStateOf<List<Schedule>>(emptyList()) }

    LaunchedEffect(Unit) {
        schedule = db.scheduleDao().getAll()
        schedule = schedule.sortedBy { StringToTime(it.start) }
        for (item in schedule){
            when (StringToTime(item.start).toLocalDate()) {
                today -> todayList = todayList + item
                tomorrow -> tomorrowList = tomorrowList + item
            }
        }
    }

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
                    NavigationDrawerItem(
                        label = { Text(text = "全部") },
                        selected = HomeStack.lastOrNull()?.id == "3",
                        onClick = {
                            HomeStack.removeLastOrNull()
                            HomeStack.add(ToDo("3"))
                            scope.launch { drawerState.close() }
                        }
                    )
                }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (HomeStack.isNotEmpty()) {
                            Text(
                                text = when (HomeStack.last().id) {
                                    "1" -> "今天"
                                    "2" -> "明天"
                                    "3" -> "全部"
                                    else -> error("Unknown id: ${HomeStack.last().id}")
                                }
                            )
                        } else {
                            Text("ToDo")
                        }
                    },
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        backStack.add(Add)
                    }
                ) {
                    Icon(Icons.Filled.Add, "添加")
                }
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
                            Today(todayList)
                        }

                        "2" -> NavEntry(key) {
                            Tomorrow(tomorrowList)
                        }

                        "3" -> NavEntry(key) {
                            All(schedule)
                        }
                        else -> error("Unknown key: ${key.id}")
                    }
                }
            )
        }
    }
}

@Composable
fun Today(schedule: List<Schedule>) {
    LazyColumn {
        items(schedule) { item ->
            ScheduleWidget(item.title, item.start, item.id)
        }
    }
}

@Composable
fun Tomorrow(schedule: List<Schedule>) {
    LazyColumn {
        items(schedule) { item ->
            ScheduleWidget(item.title, item.start, item.id)
        }
    }
}

@Composable
fun All(schedule: List<Schedule>){
    LazyColumn {
        items(schedule) { item ->
            ScheduleWidget(item.title, item.start, item.id)
        }
    }
}

fun StringToTime(str: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(str, formatter)
    return dateTime
}