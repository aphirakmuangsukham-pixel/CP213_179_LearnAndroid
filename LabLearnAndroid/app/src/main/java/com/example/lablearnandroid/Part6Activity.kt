package com.example.lablearnandroid

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

// 1. ViewModel สำหรับเก็บสถานะ URL
class WebViewModel : ViewModel() {
    var currentUrl by mutableStateOf("https://www.google.com")
        private set

    fun updateUrl(newUrl: String) {
        // ตรวจสอบและเติม https:// หากผู้ใช้ไม่ได้พิมพ์มา
        val formattedUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            "https://$newUrl"
        } else {
            newUrl
        }
        currentUrl = formattedUrl
    }
}

class Part6Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WebViewScreen(modifier: Modifier = Modifier, viewModel: WebViewModel = viewModel()) {
    // Local state สำหรับเก็บค่าที่กำลังพิมพ์ใน TextField
    var urlInput by remember { mutableStateOf(viewModel.currentUrl) }

    Column(modifier = modifier.fillMaxSize()) {
        // 4. TextField สำหรับพิมพ์ URL และปุ่ม 'Go'
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("URL Address") },
                singleLine = true
            )
            Button(
                onClick = { viewModel.updateUrl(urlInput) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Go")
            }
        }

        // 2. ใช้ AndroidView เพื่อฝัง WebView (Android View ดั้งเดิม)
        AndroidView(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            factory = { context ->
                // ส่วนของ Factory จะถูกเรียกใช้งานเพียงครั้งเดียวเมื่อ View ถูกสร้าง
                WebView(context).apply {
                    // 3. ตั้งค่า WebViewClient เพื่อให้เปิดลิงก์ภายในแอป ไม่เด้งไปเบราว์เซอร์อื่น
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true // เปิดใช้งาน JavaScript
                }
            },
            update = { webView ->
                // ส่วนของ Update จะถูกเรียกทุกครั้งที่ State (viewModel.currentUrl) มีการเปลี่ยนแปลง
                // 5. โหลด URL ใหม่เมื่อมีการสั่ง Update จาก ViewModel
                if (webView.url != viewModel.currentUrl) {
                    webView.loadUrl(viewModel.currentUrl)
                }
            }
        )
    }
}