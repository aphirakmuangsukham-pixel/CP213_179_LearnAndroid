package com.example.flashcard.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.flashcard.data.local.FlashCard
import com.example.flashcard.ui.FlashCardViewModel
import com.example.flashcard.ui.Screen
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavController,
    viewModel: FlashCardViewModel,
    categoryId: Int,
    categoryName: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    // Pending URI chosen by Photo Picker (before copy to internal storage)
    var pendingAddImagePath by remember { mutableStateOf<String?>(null) }
    var pendingEditImagePath by remember { mutableStateOf<String?>(null) }

    // Photo Picker launcher — for Add dialog
    val addImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = viewModel.copyImageToInternalStorage(uri)
                pendingAddImagePath = path
            }
        }
    }

    // Photo Picker launcher — for Edit dialog
    val editImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = viewModel.copyImageToInternalStorage(uri)
                pendingEditImagePath = path
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(categoryName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    pendingAddImagePath = null
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
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Image thumbnail (if any)
                                if (card.imageUri != null) {
                                    AsyncImage(
                                        model = File(card.imageUri),
                                        contentDescription = "Card image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    )
                                }
                                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                    Text("Q: ${card.frontText}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                                    Text("A: ${card.backText}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            frontText = card.frontText
                                            backText = card.backText
                                            pendingEditImagePath = card.imageUri
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
        }

        // ── Add Cards Dialog (Multi-Card) ─────────────────────────────────────────
        if (showAddCardDialog) {
            data class CardEntry(val front: String = "", val back: String = "")
            val cardEntries = remember { mutableStateListOf(CardEntry()) }
            val listState = rememberLazyListState()

            AlertDialog(
                onDismissRequest = { showAddCardDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("New Flashcards", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("${cardEntries.size}")
                        }
                    }
                },
                text = {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(cardEntries) { index, entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Card ${index + 1}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (cardEntries.size > 1) {
                                            IconButton(
                                                onClick = { cardEntries.removeAt(index) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Remove",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = entry.front,
                                        onValueChange = { cardEntries[index] = entry.copy(front = it) },
                                        label = { Text("Front (Question)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = entry.back,
                                        onValueChange = { cardEntries[index] = entry.copy(back = it) },
                                        label = { Text("Back (Answer)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                }
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = {
                                    cardEntries.add(CardEntry())
                                    scope.launch {
                                        listState.animateScrollToItem(cardEntries.size - 1)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Another Card")
                            }
                        }
                    }
                },
                confirmButton = {
                    val validCount = cardEntries.count { it.front.isNotBlank() && it.back.isNotBlank() }
                    Button(
                        enabled = validCount > 0,
                        onClick = {
                            cardEntries.forEach { entry ->
                                if (entry.front.isNotBlank() && entry.back.isNotBlank()) {
                                    viewModel.addCard(categoryId, entry.front, entry.back)
                                }
                            }
                            showAddCardDialog = false
                        }
                    ) {
                        Text(if (validCount > 1) "Add $validCount Cards" else "Add Card")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddCardDialog = false }) { Text("Cancel") }
                }
            )
        }

        // ── Edit Card Dialog ──────────────────────────────────────────────────────
        if (showEditCardDialog != null) {
            AlertDialog(
                onDismissRequest = { showEditCardDialog = null },
                title = { Text("Edit Flashcard") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = frontText,
                            onValueChange = { frontText = it },
                            label = { Text("Front (Question)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = backText,
                            onValueChange = { backText = it },
                            label = { Text("Back (Answer)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Image section
                        if (pendingEditImagePath != null) {
                            Box {
                                AsyncImage(
                                    model = File(pendingEditImagePath!!),
                                    contentDescription = "Card image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                TextButton(
                                    onClick = { pendingEditImagePath = null },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text("Remove", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    editImageLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Image (optional)")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (frontText.isNotBlank() && backText.isNotBlank()) {
                            viewModel.updateCard(
                                showEditCardDialog!!.copy(
                                    frontText = frontText,
                                    backText = backText,
                                    imageUri = pendingEditImagePath
                                )
                            )
                            showEditCardDialog = null
                        }
                    }) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditCardDialog = null }) { Text("Cancel") }
                }
            )
        }

        // ── AI Generate Dialog ────────────────────────────────────────────────────
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
