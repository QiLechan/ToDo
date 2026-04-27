package org.yuezhikong.todo.ui.schedule

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SharedTransitionScope.ScheduleInfoCard(animatedVisibilityScope: AnimatedVisibilityScope,) {
    Card(
        modifier = Modifier
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