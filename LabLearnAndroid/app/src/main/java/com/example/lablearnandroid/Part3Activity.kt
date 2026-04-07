package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DonutChartScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DonutChartScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Donut Chart Implementation",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(64.dp))

        Box(contentAlignment = Alignment.Center) {
            // 1. รับค่า Parameter เป็น List ของตัวเลขสัดส่วน
            val data = listOf(30f, 40f, 30f)
            val colors = listOf(Color(0xFF673AB7), Color(0xFF009688), Color(0xFFFF5722))
            
            DonutChart(
                data = data,
                colors = colors,
                modifier = Modifier.size(240.dp)
            )
            
            // ตกแต่งตัวเลขตรงกลาง
            Text(
                text = "100%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DonutChart(
    data: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    // ตรวจสอบความถูกต้องของข้อมูล
    if (data.size != colors.size) return
    
    // Normalize ข้อมูลให้รวมเป็นสัดส่วน 360 องศา
    val total = data.sum()
    val proportions = data.map { (it / total) * 360f }
    
    // 3. Animation: ค่อยๆ วาดตัวเองจาก 0 ไปจนถึง 360 องศา
    val animationProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500) // ระยะเวลา 1.5 วินาที
        )
    }

    Canvas(modifier = modifier) {
        var currentStartAngle = -90f // เริ่มวาดจากตำแหน่ง 12 นาฬิกา (-90 องศา)
        
        proportions.forEachIndexed { index, sweepAngle ->
            // คำนวณ Sweep Angle ตาม Progress ของ Animation
            // ให้ชิ้นส่วนต่างๆ ขยายออกไปพร้อมๆ กันจนครบวง
            val animatedSweep = sweepAngle * animationProgress.value
            
            // 2. ใช้วิธีการวาด drawArc และเว้นช่องตรงกลางด้วย Stroke (เพื่อให้เป็นโดนัท)
            drawArc(
                color = colors[index],
                startAngle = currentStartAngle,
                sweepAngle = animatedSweep,
                useCenter = false,
                style = Stroke(
                    width = 40.dp.toPx(),
                    cap = StrokeCap.Butt // ปลายตัดตรงเพื่อให้ชิ้นส่วนต่อกันสนิท
                )
            )
            
            // ขยับจุดเริ่มต้นสำหรับการวาดชิ้นถัดไป
            currentStartAngle += sweepAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonutChartPreview() {
    LabLearnAndroidTheme {
        DonutChartScreen()
    }
}