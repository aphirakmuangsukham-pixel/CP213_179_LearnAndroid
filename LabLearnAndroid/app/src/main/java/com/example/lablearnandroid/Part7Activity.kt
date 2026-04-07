package com.example.lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

/**
 * [Part7Activity] สาธิตการทำ Activity Transition Animation แบบดั้งเดิม
 * โดยใช้ startActivity และ finish ร่วมกับ Custom Animation
 */
class Part7Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        onOpenDetails = { message ->
                            // 1. สร้าง Intent และส่งค่า Extra String
                            val intent = Intent(this, Part7DetailActivity::class.java).apply {
                                putExtra("EXTRA_MESSAGE", message)
                            }
                            startActivity(intent)
                            
                            // 2. เรียกใช้ Custom Animation: Slide Up ขาเข้า
                            @Suppress("DEPRECATION")
                            overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
                        }
                    )
                }
            }
        }
    }
}

/**
 * [Part7DetailActivity] เป็น Activity หน้าที่สอง
 * ถูกประกาศไว้ในไฟล์เดียวกันเพื่อความสะดวกในการส่งต่อ Logic
 */
class Part7DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // รับค่า String จาก Intent
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "ไม่มีข้อมูลส่งมา"

        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DetailScreen(
                        message = message,
                        modifier = Modifier.padding(innerPadding),
                        onClose = {
                            // สั่งปิด Activity
                            finish()
                        }
                    )
                }
            }
        }
    }

    /**
     * 3. จัดการตอนปิด Activity (รวมถึงการกดปุ่ม Back)
     * สั่งให้ทำ Animation: Slide Down ขาออก
     */
    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        // สไลด์ออกจากจอลงข้างล่าง
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom)
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, onOpenDetails: (String) -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Activity Animation",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onOpenDetails("สวัสดีจาก Activity หลัก!") }) {
            Text(text = "Open Detail (Slide Up)")
        }
        
        Text(
            text = "โปรดจำไว้ว่าต้องไปลงทะเบียน Activity ใน Manifest ด้วยนะ!",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DetailScreen(message: String, modifier: Modifier = Modifier, onClose: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "นี่คือหน้า Detail",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "รับค่าผ่าน Intent Extra:")
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onClose) {
            Text(text = "Close (Slide Down)")
        }
    }
}