package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 1. ViewModel เพื่อเก็บสถานะรายชื่อและการโหลดข้อมูล
class ContactViewModel : ViewModel() {
    private val _contacts = MutableStateFlow<List<String>>(generateInitialNames())
    val contacts: StateFlow<List<String>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun generateInitialNames(): List<String> {
        return listOf(
            "Alice", "Adam", "Andrew", "Bob", "Ben", "Bill", "Charlie", "Chris", "Cathy",
            "David", "Daniel", "Diana", "Edward", "Ethan", "Emma", "Frank", "Finn", "Fiona",
            "George", "Grace", "Gary", "Hannah", "Henry", "Hope", "Ivan", "Isaac", "Ivy"
        ).sorted()
    }

    // 3. ฟังก์ชันจำลองการโหลดข้อมูลเพิ่ม (Pagination)
    suspend fun performLoadMore() {
        if (_isLoading.value) return
        _isLoading.value = true
        delay(2000) // หน่วงเวลา 2 วินาที
        val extraNames = listOf(
            "Jack", "James", "Jane", "Kevin", "Kyle", "Kelly", "Liam", "Leo", "Lily", 
            "Michelle", "Mike", "Mia", "Noah", "Nathan", "Nina", "Oscar", "Olivia"
        )
        _contacts.value = (_contacts.value + extraNames).sorted()
        _isLoading.value = false
    }
}

class Part2Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(modifier: Modifier = Modifier, viewModel: ContactViewModel = viewModel()) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // จัดกลุ่มชื่อตามตัวอักษรตัวแรกสำหรับ Sticky Header
    val grouped = remember(contacts) {
        contacts.groupBy { it.first().uppercaseChar() }
    }
    
    val listState = rememberLazyListState()

    // ตรวจจับเมื่อเลื่อนมาถึง Item ตัวสุดท้ายของ List
    val reachedBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 1
        }
    }

    // เมื่อถึงจุดสิ้นสุด ให้เรียกโหลดข้อมูลเพิ่ม
    LaunchedEffect(reachedBottom) {
        if (reachedBottom && !isLoading) {
            viewModel.performLoadMore()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Contact List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        // 2. LazyColumn ที่ใช้งาน stickyHeader
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            grouped.forEach { (initial, names) ->
                stickyHeader {
                    HeaderItem(initial.toString())
                }
                items(names) { name ->
                    ContactItem(name)
                }
            }

            // 4. แสดง CircularProgressIndicator เมื่อกำลังโหลด
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun ContactItem(name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = name, fontSize = 18.sp)
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }
}