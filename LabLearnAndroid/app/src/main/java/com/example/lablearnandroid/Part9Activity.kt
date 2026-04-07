package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part9Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                CollapsingAppBarScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingAppBarScreen() {
    // 1. Setup ScrollBehavior สำหรับการทำ Collapsing
    // exitUntilCollapsed จะทำให้ TopAppBar หดตัวลงจนเหลือแค่ขนาดปกติเมื่อเลื่อนขึ้น
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            // 2. เชื่อมโยง nestedScroll กับ scrollBehavior เพื่อให้การเลื่อนของ List ส่งผลต่อ TopAppBar
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // 3. ใช้ LargeTopAppBar เพื่อรองรับสถานะขยาย (Expanded) และหดตัว (Collapsed)
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Collapsing TopBar",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        // 4. แสดงรายการเนื้อหาเพื่อทดสอบการเลื่อน (Scroll)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // เพิ่มการอธิบาย Concept ไว้ที่ส่วนบนของรายการ
            item {
                CollapsingConceptCard()
            }

            // รายการ Mock data เพื่อให้สามารถเลื่อนหน้าจอได้
            items((1..50).toList()) { index ->
                ListItem(
                    headlineContent = { Text("รายการข้อมูลที่ $index", fontWeight = FontWeight.Medium) },
                    supportingContent = { Text("รายละเอียดเพิ่มเติมของรายการที่ $index สำหรับการศึกษา") }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun CollapsingConceptCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "💡 Concept: Collapsing UI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = "Collapsing UI คือรูปแบบการออกแบบที่ส่วนหัว (Header) จะเปลี่ยนสถานะจากส่วนขยายที่สวยงาม (Expanded) กลายเป็นแถบเมนูขนาดกะทัดรัด (Collapsed) เมื่อมีการเลื่อนเนื้อหาขึ้น เพื่อให้ความสำคัญกับเนื้อหาหลักของหน้านั้นมากขึ้น",
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(
                text = "ในระบบ Material 3 เราจะใช้ LargeTopAppBar ร่วมกับ Nested Scroll เพื่อดักจับเหตุการณ์การเลื่อนและนำค่ามาปรับขนาดของ TopBar แบบเรียลไทม์",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollapsingAppBarPreview() {
    LabLearnAndroidTheme {
        CollapsingAppBarScreen()
    }
}