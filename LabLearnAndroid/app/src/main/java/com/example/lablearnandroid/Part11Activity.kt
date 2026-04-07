package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme
import kotlinx.coroutines.delay

class Part11Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SkeletonLoadingScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SkeletonLoadingScreen(modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(true) }

    // 1. จำลองการโหลดข้อมูล (Simulated Loading)
    LaunchedEffect(Unit) {
        delay(3000) // ดีเลย์ 3 วินาทีเพื่อให้เห็น Skeleton ชัดเจน
        isLoading = false
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ส่วนอธิบาย Concept ของการทำ Skeleton
        SkeletonConceptCard()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(10) { index ->
                if (isLoading) {
                    // 2. แสดงรายการแบบ Skeleton (Placeholder)
                    SkeletonItem()
                } else {
                    // 3. แสดงเนื้อหาจริงเมื่อโหลดเสร็จ
                    ActualContentItem(index = index)
                }
            }
        }
    }
}

@Composable
fun SkeletonConceptCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "✨ Concept: Skeleton Loading",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "คือเทคนิคการแสดงโครงร่าง (Placeholder) ของ UI ในขณะที่ข้อมูลกำลังถูกโหลด แทนการแสดงแถบโปรเกรสหรือหน้าว่าง เพื่อลด 'Layout Shift' และเพิ่ม 'Perceived Performance' ให้แอปดูสว่างสดใสและรวดเร็วขึ้น",
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * [SkeletonItem] โครงสร้างจำลองที่มีการใส่ Shimmer Effect
 */
@Composable
fun SkeletonItem() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // วงกลมจำลองรูปโปรไฟล์
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .shimmerEffect() // <-- ใช้ Shimmer ตรงนี้
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            // แถวหัวข้อจำลอง
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            // แถวรายละเอียดจำลอง
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

/**
 * [ActualContentItem] ข้อมูลจริงที่จะปรากฏเมื่อ isLoading = false
 */
@Composable
fun ActualContentItem(index: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Dev", color = Color.White, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "หัวข้อบทความข่าวสารที่ $index",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "นี่คือเนื้อหาข้อมูลจริงที่ถูกโหลดมาสมบูรณ์แล้ว พบกับข่าวสารวงการ Android ได้ที่นี่...",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// -------------------------------------------------------------------------
// 🪄 Custom Shimmer Effect Modifier
// -------------------------------------------------------------------------

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    
    // ตั้งค่าการเลื่อนของ Offset แสง
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ),
        label = "shimmer_offset"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFEBEBEB),
                Color(0xFFD1D1D1),
                Color(0xFFEBEBEB),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}
