package com.example.flashcard.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.data.local.FlashCard
import com.example.flashcard.ui.FlashCardViewModel
import com.example.flashcard.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavController,
    viewModel: FlashCardViewModel,
    categoryId: Int,
    categoryName: String
) {
    val context = LocalContext.current

    LaunchedEffect(categoryId) {
        viewModel.loadCardsForCategory(categoryId)
    }

    val cards by viewModel.cardsForCategory.collectAsState()

    var showAddCardDialog by remember { mutableStateOf(false) }
    var showEditCardDialog by remember { mutableStateOf<FlashCard?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }
    
    var frontText by remember { mutableStateOf("") }
    var backText by remember { mutableStateOf("") }
    var aiInputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAiDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Generate with AI", tint = MaterialTheme.colorScheme.tertiary)
                    }
                    if (cards.isNotEmpty()) {
                        IconButton(onClick = {
                            navController.navigate(Screen.Study.createRoute(categoryId, categoryName))
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Study Mode", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    frontText = ""
                    backText = ""
                    showAddCardDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Card")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (cards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No flashcards yet.", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { showAiDialog = true }) {
                            Icon(Icons.Default.Star, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto-Generate with AI")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cards) { card ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text("Q: ${card.frontText}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                Text("A: ${card.backText}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        frontText = card.frontText
                                        backText = card.backText
                                        showEditCardDialog = card
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { viewModel.deleteCard(card) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Dialog
        if (showAddCardDialog) {
            AlertDialog(
                onDismissRequest = { showAddCardDialog = false },
                title = { Text("New Flashcard") },
                text = {
                    Column {
                        OutlinedTextField(value = frontText, onValueChange = { frontText = it }, label = { Text("Front (Question)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = backText, onValueChange = { backText = it }, label = { Text("Back (Answer)") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (frontText.isNotBlank() && backText.isNotBlank()) {
                            viewModel.addCard(categoryId, frontText, backText)
                            showAddCardDialog = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCardDialog = false }) { Text("Cancel") }
                }
            )
        }

        // Edit Dialog
        if (showEditCardDialog != null) {
            AlertDialog(
                onDismissRequest = { showEditCardDialog = null },
                title = { Text("Edit Flashcard") },
                text = {
                    Column {
                        OutlinedTextField(value = frontText, onValueChange = { frontText = it }, label = { Text("Front (Question)") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = backText, onValueChange = { backText = it }, label = { Text("Back (Answer)") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (frontText.isNotBlank() && backText.isNotBlank()) {
                            viewModel.updateCard(showEditCardDialog!!.copy(frontText = frontText, backText = backText))
                            showEditCardDialog = null
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditCardDialog = null }) { Text("Cancel") }
                }
            )
        }

        // AI Generate Dialog
        if (showAiDialog) {
            AlertDialog(
                onDismissRequest = { if (!isGenerating) showAiDialog = false },
                title = { Text("Generate cards using AI ✨") },
                text = {
                    Column {
                        Text("Paste your text, article, or vocabulary list here. Gemini will extract key concepts and automatically create flashcards.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = aiInputText,
                            onValueChange = { aiInputText = it },
                            label = { Text("Source Text") },
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            enabled = !isGenerating
                        )
                        if (isGenerating) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = !isGenerating && aiInputText.isNotBlank(),
                        onClick = {
                            isGenerating = true
                            viewModel.generateFlashcardsWithAi(aiInputText, categoryId) { success, message ->
                                isGenerating = false
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    showAiDialog = false
                                    aiInputText = ""
                                }
                            }
                        }
                    ) { Text(if (isGenerating) "Generating..." else "Generate") }
                },
                dismissButton = {
                    TextButton(onClick = { showAiDialog = false }, enabled = !isGenerating) { Text("Cancel") }
                }
            )
        }
    }
}
