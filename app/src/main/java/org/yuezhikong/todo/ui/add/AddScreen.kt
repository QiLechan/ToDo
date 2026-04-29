package org.yuezhikong.todo.ui.add

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.yuezhikong.todo.DBViewModel
import org.yuezhikong.todo.database.saveSchedule
import org.yuezhikong.todo.ui.widget.ChooseWidget
import org.yuezhikong.todo.ui.widget.DatePickerWidget
import org.yuezhikong.todo.ui.widget.SwitchWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    backStack: SnapshotStateList<Any>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedContentScope
) {
    var name by remember { mutableStateOf("") }
    var allDay by remember { mutableStateOf(false) }
    var alarm by remember { mutableStateOf(false) }
    var noticeTimes = remember { mutableStateListOf(1) }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dbvm: DBViewModel = viewModel()
    val db = dbvm.getDataBase()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "添加日程") },
            )
        },
        floatingActionButton = {
            with(sharedTransitionScope) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            saveSchedule(db, name, allDay, alarm, start, end, description, noticeTimes)
                        }
                        backStack.removeLastOrNull()
                    },
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "add-button"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                ) {
                    Icon(Icons.Filled.Check, "添加")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),//.height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                )
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    SwitchWidget(title = "全天事件", checked = allDay, onCheckedChange = { allDay = it})
                    DatePickerWidget("开始时间", onDateSelected = { start = it })
                    DatePickerWidget("结束时间", onDateSelected = { end = it })
                }
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ){
                    ChooseWidget("提醒") {
                        NoticeSettings(
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
                            current = noticeTimes)
                    }
                    if (0 !in noticeTimes) {
                        SwitchWidget(
                            title = "闹钟提醒",
                            checked = alarm,
                            onCheckedChange = { alarm = it })
                    }
                }
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                )
            }
        }
    }
}

@Composable
fun NoticeSettings(current: SnapshotStateList<Int>, noticetime: (Int) -> Unit = {} ) {
    Column {
        OptionItem("无提醒", selected = 0 in current) {
            noticetime(0)
        }
        OptionItem("日程开始时", selected = 1 in current) {
            noticetime(1)
        }
        OptionItem("日程前5分钟", selected = 2 in current) {
            noticetime(2)
        }
        OptionItem("日程前15分钟", selected = 3 in current) {
            noticetime(3)
        }
        OptionItem("日程前30分钟", selected = 4 in current) {
            noticetime(4)
        }
        OptionItem("日程前1小时", selected = 5 in current) {
            noticetime(5)
        }
    }
}

@Composable
fun OptionItem(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(
            onClick = { onClick() }
        ),
        trailingContent = {
            if (selected) { Icon(Icons.Default.Check, contentDescription = "Selected") }
        },
    )
}