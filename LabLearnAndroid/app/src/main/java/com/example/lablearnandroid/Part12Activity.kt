package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
class Part12Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OverlayDemoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlayDemoScreen(modifier: Modifier = Modifier) {
    // 1. States สำหรับควบคุมการแสดงผลของ Overlays
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    
    // State สำหรับจัดการพฤติกรรมของ Bottom Sheet (เช่น การยุบ/ขยาย)
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // ส่วนอธิบาย Concept
        ConceptExplanationCard()

        Spacer(modifier = Modifier.height(48.dp))

        // 2. Button สำหรับเปิด Modal Bottom Sheet
        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "เปิด Modal Bottom Sheet")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Button สำหรับเปิด Middle Dialog (AlertDialog)
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "เปิด Middle Dialog (AlertDialog)")
        }
    }

    // 4. การจัดการ Modal Bottom Sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            // เนื้อหาภายใน Bottom Sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "นี่คือ Modal Bottom Sheet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "เป็นพื้นที่สำหรับตัวเลือกเพิ่มเติม หรือฟอร์มที่ต้องการความรวดเร็ว ผู้ใช้สามารถปัดหน้าจอลงเพื่อปิด (Dismiss) ได้อย่างเป็นธรรมชาติ",
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showBottomSheet = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ตกลงและปิด")
                }
            }
        }
    }

    // 5. การจัดการ Middle Dialog (AlertDialog)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = "ข้อมูลยืนยัน", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "Middle Dialog (หรือ AlertDialog) จะปรากฏขึ้นกลางหน้าจอเพื่อดึงความสนใจของผู้ใช้อย่างเต็มที่ มักใช้สำหรับการแจ้งเตือนที่สำคัญหรือการยืนยันการกระทำที่ไม่สามารถย้อนกลับได้")
            },
            icon = {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}

@Composable
fun ConceptExplanationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "✨ Concept: Overlays",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "1. Modal Bottom Sheet: เน้นความต่อเนื่อง (Contextual) มักใช้กับรายการตัวเลือก หรือแอ็คชันเสริม\n\n" +
                        "2. Middle Dialog: เน้นการขัดจังหวะ (Interruptive) มักใช้เพื่อความปลอดภัยหรือการแจ้งเตือนที่พลาดไม่ได้",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}