package org.yuezhikong.todo.ui.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddScreen() {
    Scaffold {paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) { Text("添加界面") }
    }
}