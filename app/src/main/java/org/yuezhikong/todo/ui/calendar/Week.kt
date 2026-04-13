package org.yuezhikong.todo.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Week() {
    val start = 1
    val end = 7
    val isSelected = false
    Row {
        for (day in start..end) {
            if (day == 3) {
                Day(day, true)
            } else
            Day(day, isSelected)
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}