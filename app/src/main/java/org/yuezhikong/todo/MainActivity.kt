package org.yuezhikong.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.yuezhikong.todo.ui.add.AddScreen
import org.yuezhikong.todo.ui.home.CalendarScreen
import org.yuezhikong.todo.ui.home.HomeScreen
import org.yuezhikong.todo.ui.home.UserScreen
import org.yuezhikong.todo.ui.schedule.ScheduleInfoScreen
import org.yuezhikong.todo.ui.theme.ToDoTheme

data object Home
data object Add
data class ScheduleDetail(val id: String)

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

sealed class Tab(val title: String, val icon: ImageVector) {
    object Home : Tab("首页", Icons.Default.Home)
    object Calendar : Tab("日历", Icons.Default.DateRange)
    object User : Tab("我的", Icons.Default.Person)
}

val tabs = listOf(Tab.Home, Tab.Calendar, Tab.User)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDo() {
    val backStack = remember { mutableStateListOf<Any>(Home) }
    val selectedHomeFilter = rememberSaveable { mutableStateOf("1") }
    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier
                .fillMaxSize(),
            transitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith ExitTransition.None
            },
            entryProvider = { key ->
                when (key) {
                    Home -> NavEntry(key) {
                        MainPage(
                            backStack = backStack,
                            selectedHomeFilter = selectedHomeFilter.value,
                            onHomeFilterChange = { selectedHomeFilter.value = it },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        )
                    }

                    Add -> NavEntry(key) {
                        AddScreen(backStack)
                    }

                    is ScheduleDetail -> NavEntry(key) {
                        ScheduleInfoScreen(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            scheduleId = key.id,
                            onBackPressed = { backStack.removeLastOrNull() },
                        )
                    }
                    else -> error("Unknown key")
                }
            }
        )
    }
}

@Composable
fun MainPage(
    backStack: SnapshotStateList<Any>,
    selectedHomeFilter: String,
    onHomeFilterChange: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = { BottomBar(pagerState, scope) }
    ){ paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (tabs[page]) {
                Tab.Home -> HomeScreen(
                    backStack = backStack,
                    selectedHomeFilter = selectedHomeFilter,
                    onHomeFilterChange = onHomeFilterChange,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                Tab.Calendar -> CalendarScreen()
                Tab.User -> UserScreen()
            }
        }
    }
}

@Composable
fun BottomBar(pagerState: PagerState, scope: CoroutineScope) {
    NavigationBar {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                          },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) }
            )
        }
    }
}