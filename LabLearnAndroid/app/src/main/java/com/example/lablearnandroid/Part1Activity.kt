package com.example.lablearnandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

class Part1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LikeButtonScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LikeButtonScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LikeButton()
    }
}

@Composable
fun LikeButton() {
    // State สำหรับสถานะปุ่ม (Liked / Not Liked)
    var isLiked by remember { mutableStateOf(false) }
    
    // InteractionSource สำหรับตรวจสอบการกด (Press state) เพื่อทำ Scale Animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 1. Scale animation: เมื่อปุ่มถูกกด จะมีขนาดใหญ่ขึ้นเล็กน้อย (1.1f) พอปล่อยจะเด้งกลับมา 1f โดยมี Spring effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scaleAnimation"
    )

    // 2. Color animation: เปลี่ยนสีพื้นหลังปุ่มจากเทา เป็น ชมพู
    val buttonColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFE91E63) else Color.Gray,
        label = "colorAnimation"
    )

    Button(
        onClick = { isLiked = !isLiked },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier.scale(scale)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 3. AnimatedVisibility: โชว์ไอคอนรูปหัวใจพร้อม Animation พื้นฐานเมื่อเปลี่ยนสถานะเป็น Liked
            AnimatedVisibility(visible = isLiked) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Heart Icon",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Text(text = if (isLiked) "Liked" else "Like", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LikeButtonPreview() {
    LabLearnAndroidTheme {
        LikeButtonScreen()
    }
}