package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part8Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ResponsiveProfileScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ResponsiveProfileScreen(modifier: Modifier = Modifier) {
    // 1. ใช้ BoxWithConstraints เพื่อตรวจสอบขนาดความกว้างของพื้นที่ที่มีให้ (maxWidth)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // เงื่อนไขในการเลือก Layout
        if (maxWidth < 600.dp) {
            // 2. หากความกว้าง < 600.dp (แนวตั้ง / Mobile)
            MobileProfileLayout()
        } else {
            // 3. หากความกว้าง >= 600.dp (แนวนอน / Tablet)
            TabletProfileLayout()
        }
    }
}

@Composable
fun MobileProfileLayout() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(modifier = Modifier.size(150.dp))
        Spacer(modifier = Modifier.height(24.dp))
        ProfileInfo(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun TabletProfileLayout() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ใช้ weight เพื่อแบ่งพื้นที่ (รูป 1 ส่วน : ข้อมูล 2 ส่วน)
        ProfileImage(modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.width(32.dp))
        ProfileInfo(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileImage(modifier: Modifier = Modifier) {
    // กล่องสีเทาสมมติแทนรูปโปรไฟล์
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Pic", color = Color.DarkGray)
    }
}

@Composable
fun ProfileInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "John Doe",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Senior Android Developer",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Experienced developer with a passion for building beautiful and functional mobile applications using Jetpack Compose.",
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun MobilePreview() {
    LabLearnAndroidTheme {
        ResponsiveProfileScreen()
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun TabletPreview() {
    LabLearnAndroidTheme {
        ResponsiveProfileScreen()
    }
}