package org.yuezhikong.todo.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.yuezhikong.todo.DBViewModel
import org.yuezhikong.todo.ui.calendar.Day
import org.yuezhikong.todo.ui.calendar.Month
import org.yuezhikong.todo.ui.calendar.Week
import java.time.DayOfWeek
import java.time.LocalDate

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var selectedDay by remember { mutableIntStateOf(0) }
    var collapsed by remember { mutableStateOf(false) }
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
    val listState = rememberLazyListState()
    var markedList by remember { mutableStateOf<List<Int>>(emptyList()) }
    val dbvm: DBViewModel = viewModel()
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentDisplayDate) {
        markedList = emptyList()
        val currentYear = currentDisplayDate.year
        val currentMonth = currentDisplayDate.monthValue
        val days = currentDisplayDate.lengthOfMonth()
        val start = (currentYear.toString() + currentMonth.toString().padStart(2, '0') + "01").toInt()
        val end = start + days - 1
        val monthScheduleList = dbvm.getByStartDateRange(start, end)

        for (day in monthScheduleList){
            day.start_date.toString().takeLast(2).toIntOrNull()?.let {
                if (it in 1..days) {
                    markedList = markedList + it
                }
            }
        }
    }

    LaunchedEffect(listState) {
        var lastOffset = 0
        var lastIndex = 0

        snapshotFlow {
            listState.firstVisibleItemIndex to
                    listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val isScrollingDown =
                index > lastIndex ||
                        (index == lastIndex && offset > lastOffset)
            val isScrollingUp =
                index < lastIndex ||
                        (index == lastIndex && offset < lastOffset)

            if (isScrollingDown) {
                collapsed = true
            }
            if (isScrollingUp &&
                index == 0 &&
                offset < 10
            ) {
                collapsed = false
            }
            lastIndex = index
            lastOffset = offset
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
                AnimatedContent(
                    targetState = collapsed,
                    label = ""
                ) { isCollapsed ->
                    if (isCollapsed) {
                        val date by remember { mutableStateOf(LocalDate.of(currentDisplayDate.year, currentDisplayDate.month, selectedDay)) }
                        var isMonthChanged by remember { mutableStateOf(false) }
                        Week(
                            date.with(DayOfWeek.MONDAY).dayOfMonth,
                            date.with(DayOfWeek.SUNDAY).dayOfMonth,
                            selectedDay,
                            markedList,
                            isMonthChanged = { isMonthChanged = it },
                        ){
                            if (isMonthChanged) {
                                if (it > 7) {
                                    date.minusMonths(1)
                                    scope.launch { pagerState.scrollToPage(pagerState.currentPage - 1) }
                                    selectedDay = it
                                } else {
                                    date.plusMonths(1)
                                    scope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                                    selectedDay = it
                                }
                            }
                            else
                                selectedDay = it
                        }
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        ) { page ->
                            Month(
                                today.plusMonths(page - centerPage.toLong()),
                                selectedDay,
                                markedList,
                                onValueChange = { selectedDay = it }
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = listState
                ) {
                    items(20) { index ->
                        Text(
                            text = "日程 $index",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}