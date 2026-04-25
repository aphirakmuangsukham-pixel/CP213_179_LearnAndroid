package com.example.flashcard.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.ui.FlashCardViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ChallengeGameScreen(navController: NavController, viewModel: FlashCardViewModel, mode: String) {
    val cards by viewModel.challengeCards.collectAsState()
    val timeLeft by viewModel.challengeTimeLeft.collectAsState()

    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    // End Time Attack when time is 0
    LaunchedEffect(timeLeft) {
        if (mode == "time_attack" && timeLeft == 0 && cards.isNotEmpty() && !isGameOver) {
            isGameOver = true
        }
    }

    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentCard = cards.getOrNull(currentIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    when (mode) {
                        "quiz" -> Text("Quiz Mode")
                        "time_attack" -> Text("Time Attack")
                        "daily" -> Text("Daily Challenge")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    if (mode == "time_attack") {
                        Text(
                            text = "${timeLeft}s",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (timeLeft <= 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isGameOver || currentIndex >= cards.size) {
                // Game Over Screen
                Spacer(modifier = Modifier.height(32.dp))
                Text("Challenge Complete!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your Score", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "$correctCount", 
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (mode != "time_attack") {
                            Text("out of ${cards.size}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
                
                Button(
                    onClick = { 
                        viewModel.finishStudySession(0, correctCount, cards.size) // 0 as a dummy category id to save stats
                        navController.popBackStack() 
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Finish", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            } else if (currentCard != null) {
                // Progress
                if (mode != "time_attack") {
                    LinearProgressIndicator(
                        progress = currentIndex.toFloat() / cards.size,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Card ${currentIndex + 1} of ${cards.size}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Score: $correctCount", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { isFlipped = !isFlipped },
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AnimatedContent(targetState = isFlipped, label = "flip") { flipped ->
                            if (flipped) {
                                Text(
                                    text = currentCard.backText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                Text(
                                    text = currentCard.frontText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                if (isFlipped) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                isFlipped = false
                                currentIndex++
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Incorrect")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                correctCount++
                                isFlipped = false
                                currentIndex++
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Correct")
                        }
                    }
                } else {
                    Button(
                        onClick = { isFlipped = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Show Answer")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
