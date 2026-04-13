package org.yuezhikong.todo.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Day(day: Int, isSelected: Boolean = false) {
    Box(
        modifier = Modifier
            .height(30.dp)
            .width(30.dp)
            .clip(CircleShape)
            .background(
                if (isSelected)
                MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = day.toString(),
            color = Color.White
        )
    }
}