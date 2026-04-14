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
import androidx.compose.runtime.derivedStateOf
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
    val centerPage = dummyPageCount / 2
    val today = LocalDate.now()
    selectedDay = if (selectedDay == 0) today.dayOfMonth else selectedDay
    val pagerState = rememberPagerState(
        pageCount = { dummyPageCount },
        initialPage = centerPage
    )
    val currentDisplayDate by remember(today, pagerState) {
        derivedStateOf {
            today.plusMonths((pagerState.currentPage - centerPage).toLong())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "${currentDisplayDate.year}年${currentDisplayDate.monthValue}月") },
            )
        }
    ) { paddingValues ->
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
                        .height(300.dp)
                ) { page ->
                    Month(
                        today.plusMonths(page - centerPage.toLong()),
                        selectedDay,
                        onValueChange = { selectedDay = it }
                    )
                }
            }
        }
    }
}