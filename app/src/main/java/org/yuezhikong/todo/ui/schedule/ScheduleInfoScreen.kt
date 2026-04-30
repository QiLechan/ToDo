package org.yuezhikong.todo.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.yuezhikong.todo.DBViewModel
import org.yuezhikong.todo.database.Schedule
import org.yuezhikong.todo.database.editSchedule
import org.yuezhikong.todo.ui.add.NoticeSettings
import org.yuezhikong.todo.ui.widget.ChooseWidget
import org.yuezhikong.todo.ui.widget.SwitchWidget

private fun parseNoticeTimes(raw: String): List<Int> {
    return raw
        .removePrefix("[")
        .removeSuffix("]")
        .split(",")
        .mapNotNull { it.trim().toIntOrNull() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInfoScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    scheduleId: String,
    onBackPressed: () -> Unit,
) {
    val dbvm: DBViewModel = viewModel()
    val scope = rememberCoroutineScope()
    var schedule by remember { mutableStateOf<Schedule?>(null) }
    var alarmEnabled by remember { mutableStateOf(false) }
    var cardVisible by remember { mutableStateOf(true) }
    var rowVisible by remember { mutableStateOf(true) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        contentVisible = true
    }

    LaunchedEffect(scheduleId) {
        schedule = dbvm.getDataBase().scheduleDao().getById(scheduleId.toInt())
        alarmEnabled = schedule?.alarm ?: false
    }

    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "日程详情") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AnimatedVisibility(
                        visible = cardVisible,
                        exit = slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300)),
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(600.dp)
                                .padding(32.dp)
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "card-$scheduleId"),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                        ) {
//                            AnimatedVisibility(
//                                visible = contentVisible,
//                                enter = fadeIn(animationSpec = tween(300))
//                            ) {
                            schedule?.let { sch ->
                                val format_time =
                                    sch.start.substring(0, 4) + "年" + sch.start.substring(
                                        4,
                                        6
                                    ) + "月" + sch.start.substring(
                                        6,
                                        8
                                    ) + "日" + sch.start.substring(
                                        8,
                                        10
                                    ) + ":" + sch.start.substring(10, 12)
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(
                                                    key = "title-$scheduleId"
                                                ),
                                                animatedVisibilityScope = animatedVisibilityScope,
                                            ),
                                        text = sch.title
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                            .sharedElement(
                                                sharedContentState = rememberSharedContentState(
                                                    key = "time-$scheduleId"
                                                ),
                                                animatedVisibilityScope = animatedVisibilityScope
                                            ),
                                        text = format_time
                                    )
                                    if (contentVisible) {
                                        HorizontalDivider(thickness = 1.dp)
                                        Text(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            ),
                                            text = sch.description
                                        )
                                        val noticeTimes = remember(sch.noticeTimes) {
                                            parseNoticeTimes(sch.noticeTimes).toMutableStateList()
                                        }
                                        ChooseWidget("提醒") {
                                            NoticeSettings(
                                                current = noticeTimes,
                                                noticetime = {
                                                    if (it == 0){
                                                        noticeTimes.clear()
                                                        noticeTimes.add(0)
                                                    }
                                                    else {
                                                        if (it in noticeTimes) {
                                                            noticeTimes.remove(it)
                                                        } else {
                                                            noticeTimes.remove(0)
                                                            noticeTimes.add(it)
                                                        }
                                                    }
                                                },
                                                onChange = {
                                                    val updatedSchedule = sch.copy(noticeTimes = noticeTimes.joinToString(","))
                                                    schedule = updatedSchedule
                                                    scope.launch {
                                                        editSchedule(
                                                            dbvm.getDataBase(),
                                                            updatedSchedule
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                        SwitchWidget(
                                            title = "闹钟",
                                            checked = alarmEnabled,
                                            onCheckedChange = { checked ->
                                                alarmEnabled = checked
                                                val updatedSchedule = sch.copy(alarm = checked)
                                                schedule = updatedSchedule
                                                scope.launch {
                                                    editSchedule(
                                                        dbvm.getDataBase(),
                                                        updatedSchedule
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            //}
                        }
                    }
                    AnimatedVisibility(
                        visible = rowVisible,
                        exit = slideOutVertically(
                            targetOffsetY = { it * 2 },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                48.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            FloatingActionButton(
                                onClick = {},
                                shape = CircleShape
                            ) {
                                Icon(Icons.Filled.Edit, "修改")
                            }
                            FloatingActionButton(
                                onClick = {
                                    cardVisible = false
                                    rowVisible = false
                                    scope.launch {
                                        delay(320)
                                        schedule?.let {
                                            dbvm.getDataBase().scheduleDao().delete(it)
                                        }
                                        onBackPressed()
                                    }
                                },
                                shape = CircleShape
                            ) {
                                Icon(Icons.Filled.Delete, "删除")
                            }
                        }
                    }
                }
            }
        }
    }
}