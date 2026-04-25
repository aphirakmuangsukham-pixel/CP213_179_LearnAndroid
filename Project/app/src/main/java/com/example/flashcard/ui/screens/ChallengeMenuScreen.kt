package com.example.flashcard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.ui.FlashCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeMenuScreen(navController: NavController, viewModel: FlashCardViewModel) {
    val context = LocalContext.current
    
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    var selectedCategoryId by remember { mutableIntStateOf(-1) } // -1 means All Decks
    var expanded by remember { mutableStateOf(false) }
    
    var showTimeAttackDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableIntStateOf(60) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenges") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Test Your Knowledge",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Select a mode to challenge yourself. You need at least 5 cards in the selected deck to play.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Deck Selection
            Text("Select Deck:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopStart)) {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    val selectedName = if (selectedCategoryId == -1) "All Decks" else categories.find { it.id == selectedCategoryId }?.name ?: "All Decks"
                    Text(selectedName)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("All Decks") },
                        onClick = { selectedCategoryId = -1; expanded = false }
                    )
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = { selectedCategoryId = category.id; expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ChallengeCard(
                title = "Quiz Mode",
                description = "Practice with 20 random cards from the selected deck. No time limit.",
                icon = Icons.Default.Lightbulb,
                color = MaterialTheme.colorScheme.primaryContainer,
                onColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    viewModel.startQuizMode(selectedCategoryId) { isReady ->
                        if (isReady) {
                            navController.navigate("challenge_game/quiz")
                        } else {
                            Toast.makeText(context, "Not enough cards! Add at least 5 cards.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

            ChallengeCard(
                title = "Time Attack",
                description = "Race against the clock! Answer as many flashcards as you can.",
                icon = Icons.Default.Timer,
                color = MaterialTheme.colorScheme.errorContainer,
                onColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = { showTimeAttackDialog = true }
            )

            ChallengeCard(
                title = "Daily Challenge",
                description = "10 fixed cards for today. Your goal is 100% accuracy. Are you up for it?",
                icon = Icons.Default.EmojiEvents,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                onColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = {
                    viewModel.startDailyChallenge(selectedCategoryId) { isReady ->
                        if (isReady) {
                            navController.navigate("challenge_game/daily")
                        } else {
                            Toast.makeText(context, "Not enough cards! Add at least 5 cards.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        if (showTimeAttackDialog) {
            AlertDialog(
                onDismissRequest = { showTimeAttackDialog = false },
                title = { Text("Select Time Limit") },
                text = {
                    Column {
                        listOf(30, 60, 120).forEach { time ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTime = time }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedTime == time, onClick = { selectedTime = time })
                                Text("$time Seconds", modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showTimeAttackDialog = false
                        viewModel.startTimeAttack(selectedCategoryId, selectedTime) { isReady ->
                            if (isReady) {
                                navController.navigate("challenge_game/time_attack")
                            } else {
                                Toast.makeText(context, "Not enough cards in this deck!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Text("Start")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimeAttackDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun ChallengeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = onColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = onColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = onColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
