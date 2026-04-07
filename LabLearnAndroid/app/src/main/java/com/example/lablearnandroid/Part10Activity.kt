package com.example.lablearnandroid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text as GlanceText
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

/**
 * [Part10Activity] ทำหน้าที่เป็นหน้าจอหลักสำหรับอธิบาย Concept ของ App Widget และ Jetpack Glance
 */
class Part10Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EducationalScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun EducationalScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "การศึกษาเรื่อง App Widget และ Jetpack Glance",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "💡 Concept ของ App Widget:\n" +
                    "App Widgets คือส่วนของแอปที่สามารถนำไปติดไว้ที่ Home Screen เพื่อให้ผู้ใช้เข้าถึงข้อมูลสำคัญได้ทันที (เช่น สภาพอากาศ, ตารางงาน) โดยไม่ต้องเปิดแอปเต็มรูปแบบ",
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "🚀 ทำไมต้อง Jetpack Glance?\n" +
                    "เดิมการเขียน Widget ต้องใช้ RemoteViews ที่ยุ่งยาก แต่ Glance ช่วยให้เราเขียน UI ของ Widget ด้วยสไตล์ Declarative (เหมือน Compose) ทำให้โค้ดอ่านง่ายและรักษาได้ง่ายขึ้น",
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.padding(top = 24.dp))
        Text(
            text = "🛠 โครงสร้างคลาสในไฟล์นี้:\n" +
                    "1. MyGlanceWidget: กำหนดหน้าตา UI ของ Widget\n" +
                    "2. MyGlanceReceiver: ตัวรับสัญญาณ (Receiver) เมื่อระบบต้องการอัปเดต Widget",
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// -------------------------------------------------------------------------
// ส่วนของ Jetpack Glance Widget (ส่วนที่แสดงบน Home Screen)
// -------------------------------------------------------------------------

/**
 * [MyGlanceWidget] คือคลาสที่นิยามหน้าตา UI ของ Widget
 */
class MyGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // หมายเหตุ: Glance ใช้องค์ประกอบ UI เฉพาะตัว (ไม่ใช่ Compose Normal UI)
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(android.graphics.Color.BLACK))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                GlanceText(
                    text = "Hello Glance!",
                    style = TextStyle(
                        color = ColorProvider(android.graphics.Color.WHITE),
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}

/**
 * [MyGlanceReceiver] เป็น BroadcastReceiver ที่ทำหน้าที่ส่งข้อมูล UI ไปยัง Home Screen
 * ต้องทำการลงทะเบียนตัวนี้ใน AndroidManifest.xml
 */
class MyGlanceReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyGlanceWidget()
}