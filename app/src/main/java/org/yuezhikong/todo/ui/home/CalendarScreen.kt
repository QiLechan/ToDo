package org.yuezhikong.todo.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.yuezhikong.todo.ui.calendar.Day
import org.yuezhikong.todo.ui.calendar.Month
import java.time.LocalDate

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var selectedDay by remember { mutableIntStateOf(0) }
    val dummyPageCount = Int.MAX_VALUE
    val today = LocalDate.now()
    selectedDay = if (selectedDay == 0) today.dayOfMonth else selectedDay
    val year = today.year
    val month = today.month
    val pagerState = rememberPagerState(
        pageCount = { dummyPageCount },
        initialPage = dummyPageCount / 2
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "${year}年${month.value}月") },
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                    .fillMaxSize()
            ) {
                Day()
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                ) { page ->
                    Month(
                        today.plusMonths(page - dummyPageCount / 2L),
                        selectedDay,
                        onValueChange = { selectedDay = it }
                    )
                }
            }
        }
    }
}