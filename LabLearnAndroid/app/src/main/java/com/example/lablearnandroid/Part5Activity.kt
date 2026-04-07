package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * [SideEffectViewModel] จัดการเหตุการณ์ที่เกิดขึ้นเพียงครั้งเดียว (One-time event)
 * เราใช้ SharedFlow เพื่อส่งข้อความ Error แทนการใช้ UI State ปกติ
 * เพื่อป้องกันปัญหา Snackbar แสดงซ้ำเวลาทำ Recomposition
 */
class SideEffectViewModel : ViewModel() {
    // 1. ใช้ SharedFlow สำหรับส่ง One-time Event (Error Message)
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun triggerError() {
        viewModelScope.launch {
            // จำลองการเกิด Error จากฝั่ง ViewModel / Data Layer
            _errorFlow.emit("เกิดข้อผิดพลาดในการเชื่อมต่อ (Mock Error)!")
        }
    }
}

class Part5Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                SideEffectScreen()
            }
        }
    }
}

@Composable
fun SideEffectScreen(viewModel: SideEffectViewModel = viewModel()) {
    // 2. SnackbarHostState สำหรับควบคุมการแสดง Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // 3. หัวใจสำคัญ: ใช้ LaunchedEffect เพื่อ Observe ค่าจาก SharedFlow
    // LaunchedEffect ทำหน้าที่รันโค้ดที่เป็น Side Effect ใน Coroutine scope ที่ผูกกับ Lifecycle ของ Composable นี้
    // ข้อดี: โค้ดใน collect จะทำงานเพียงครั้งเดียวต่อหนึ่งการ emit และไม่รันใหม่ถ้า UI อัปเดตส่วนอื่น
    LaunchedEffect(viewModel.errorFlow) {
        viewModel.errorFlow.collect { errorMessage ->
            // เมื่อได้รับ Event จาก ViewModel ให้สั่งแสดง Snackbar
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "รับทราบ"
            )
        }
    }

    Scaffold(
        // ผูก SnackbarHost เข้ากับ Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // 4. ปุ่ม Trigger Error เพื่อทดสอบการทำงาน
            Button(onClick = { viewModel.triggerError() }) {
                Text(text = "Trigger Error")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SideEffectScreenPreview() {
    LabLearnAndroidTheme {
        SideEffectScreen()
    }
}