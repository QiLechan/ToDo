package org.yuezhikong.todo.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.Month
import java.time.Year

@Composable
fun Month(year: Int, month: Month, selected: Int = 0) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val firstDay = LocalDate.of(year, month, 1).dayOfWeek.value
        val days = month.length(Year.of(year).isLeap)
        Week(1, 7 - firstDay + 1, firstDay, selected)
        var day = 8 - firstDay
        while (day <= days) {
            Week(day, minOf(day + 6, days), 1, selected)
            day += 7
        }
    }
}

@Preview
@Composable
fun Test(){
    Month(2026, Month.APRIL , 13)
}