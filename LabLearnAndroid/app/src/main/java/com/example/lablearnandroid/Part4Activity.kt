package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay

// 1. ViewModel ที่เก็บ State รายการ To-Do ด้วย mutableStateListOf
class TodoViewModel : ViewModel() {
    val todoList = mutableStateListOf(
        "ซื้อของเข้าบ้าน",
        "ส่งงานโปรเจกต์ Android",
        "พาสุนัขไปเดินเล่น",
        "อ่านหนังสือเรื่อง Compose",
        "จัดห้องนอน"
    )

    fun removeTodo(todo: String) {
        todoList.remove(todo)
    }
}

class Part4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TodoScreen(modifier: Modifier = Modifier, viewModel: TodoViewModel = viewModel()) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "To-Do List (Swipe to Dismiss)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        // ใช้ LazyColumn ในการแสดงรายการ
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = viewModel.todoList,
                key = { it } // กำหนด KEY เพื่อให้ Animation ทำงานถูกต้องเวลาลบ
            ) { todo ->
                TodoItem(
                    task = todo,
                    onDelete = { viewModel.removeTodo(todo) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoItem(task: String, onDelete: () -> Unit) {
    var isRemoved by remember { mutableStateOf(false) }
    
    // 2. ใช้ SwipeToDismissBox (Material 3)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) { // 3. ปัดไปทางซ้าย (End to Start)
                isRemoved = true
                true
            } else {
                false
            }
        },
        positionalThreshold = { it * 0.4f } // ปัดเกิน 40% ให้ถือว่า Dismiss
    )

    // เมื่อสถานะเปลี่ยนเป็นถูกลบ ให้หน่วงเวลา Animation นิดหน่อยก่อนลบออกจาก List
    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(300)
            onDelete()
        }
    }

    // มี Animation เวลา Item ค่อยๆ หายไป
    AnimatedVisibility(
        visible = !isRemoved,
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            enableDismissFromStartToEnd = false, // ไม่เปิดปัดขวา
            backgroundContent = {
                // 3. พื้นหลังสีแดงและ Icon รูปถังขยะ
                val color = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.Transparent
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = Color.White
                    )
                }
            },
            content = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = task,
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    LabLearnAndroidTheme {
        TodoScreen()
    }
}