package org.yuezhikong.todo.ui.schedule

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.yuezhikong.todo.DBViewModel
import org.yuezhikong.todo.database.AppDatabase.Companion.getDatabase
import org.yuezhikong.todo.database.Schedule
import org.yuezhikong.todo.ui.home.StringToTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInfoScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    scheduleId: String,
    onBackPressed: () -> Unit,
    ) {
    var schedule by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    val dbvm: DBViewModel = viewModel()

    LaunchedEffect(Unit) {
        schedule = dbvm.getAllSchedules()
    }

    val item = schedule.find { it.id == scheduleId.toInt() }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "日程详情") })
            }
        ){ paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .padding(32.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "card-$scheduleId"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                ) {
                    if (item != null){
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = "id=" + item.id
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = "title" + item.title
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = "start" + item.start
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = "end" + item.end
                        )
                    }
                }
            }
        }
    }
}