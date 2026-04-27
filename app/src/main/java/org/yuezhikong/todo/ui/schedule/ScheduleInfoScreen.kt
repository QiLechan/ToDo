package org.yuezhikong.todo.ui.schedule

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInfoScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit
    ) {
    with(sharedTransitionScope) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(text = "日程详情") })
            }
        ){ paddingValues ->
            Card(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(8.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "card"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "展开卡片测试",
                )
            }
        }
    }
}