package org.yuezhikong.todo.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.yuezhikong.todo.ui.schedule.ScheduleInfoScreen

@Composable
fun ScheduleWidget(title: String, time: String, id: Int) {
    var showDetails by remember { mutableStateOf(false) }
    val format_time = time.substring(0, 4) + "年" + time.substring(4, 6) + "月" + time.substring(6, 8) + "日" + time.substring(8, 10) + ":" + time.substring(10, 12)

    SharedTransitionLayout {
        AnimatedContent(targetState = showDetails) { inDetails ->
            if (inDetails) {
                ScheduleInfoScreen(this@AnimatedContent)
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(72.dp)
                        .clickable {
                            showDetails = true
                        }
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "card"),
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            text = title,
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = format_time,
                        )
                    }
                }
            }
        }
    }
}