package org.yuezhikong.todo.ui.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.yuezhikong.todo.Add
import org.yuezhikong.todo.DBViewModel
import org.yuezhikong.todo.ScheduleDetail
import org.yuezhikong.todo.database.AppDatabase.Companion.getDatabase
import org.yuezhikong.todo.database.Schedule
import org.yuezhikong.todo.ui.widget.ScheduleWidget
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    backStack: SnapshotStateList<Any>,
    selectedHomeFilter: String,
    onHomeFilterChange: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = remember { getDatabase(context) }
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    var todayList by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var tomorrowList by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var schedule by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    val dbvm: DBViewModel = viewModel()

    LaunchedEffect(Unit) {
        schedule = dbvm.getAllSchedules()
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
                        selected = selectedHomeFilter == "1",
                        onClick = {
                            onHomeFilterChange("1")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "明天") },
                        selected = selectedHomeFilter == "2",
                        onClick = {
                            onHomeFilterChange("2")
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = "全部") },
                        selected = selectedHomeFilter == "3",
                        onClick = {
                            onHomeFilterChange("3")
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
                        Text(
                            text = when (selectedHomeFilter) {
                                "1" -> "今天"
                                "2" -> "明天"
                                "3" -> "全部"
                                else -> error("Unknown id: $selectedHomeFilter")
                            }
                        )
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
            val displayList = when (selectedHomeFilter) {
                "1" -> todayList
                "2" -> tomorrowList
                "3" -> schedule
                else -> error("Unknown id: $selectedHomeFilter")
            }
            ScheduleList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                schedule = displayList,
                onOpenDetail = { scheduleId -> backStack.add(ScheduleDetail(scheduleId)) },
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
            )
        }
    }
}

@Composable
fun LazyColumnSchedule(
    modifier: Modifier = Modifier,
    schedule: List<Schedule>,
    onOpenDetail: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    LazyColumn(modifier = modifier) {
        items(items = schedule, key = { it.id }) { item ->
            ScheduleWidget(item.title, item.start, item.id, sharedTransitionScope, animatedVisibilityScope) {
                onOpenDetail(item.id.toString())
            }
        }
    }
}

@Composable
fun ScheduleList(
    modifier: Modifier = Modifier,
    schedule: List<Schedule>,
    onOpenDetail: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    LazyColumnSchedule(
        modifier = modifier,
        schedule = schedule,
        onOpenDetail = onOpenDetail,
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedVisibilityScope,
    )
}

fun StringToTime(str: String): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
    val dateTime = LocalDateTime.parse(str, formatter)
    return dateTime
}