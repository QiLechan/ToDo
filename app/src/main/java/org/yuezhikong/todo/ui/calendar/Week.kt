package org.yuezhikong.todo.ui.calendar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Week(start: Int, end: Int, weekday: Int, selected: Int) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (i in 1 until weekday) {
            Day(0, false, Modifier.weight(1f))
        }
        for (day in start..end) {
            Day(day, day == selected, Modifier.weight(1f))
        }
        val count = (weekday - 1) + (end - start + 1)
        for (i in count until 7) {
            Day(0, false, Modifier.weight(1f))
        }
    }
}