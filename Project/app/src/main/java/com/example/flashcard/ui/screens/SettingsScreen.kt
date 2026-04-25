package com.example.flashcard.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcard.ui.FlashCardViewModel
import com.example.flashcard.ui.notifications.NotificationHelper
import java.util.Locale
import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: FlashCardViewModel) {
    val context = LocalContext.current

    // Reminders
    var isReminderEnabled by remember { mutableStateOf(viewModel.isReminderEnabled()) }
    var reminderHour by remember { mutableStateOf(viewModel.getReminderHour()) }
    var reminderMinute by remember { mutableStateOf(viewModel.getReminderMinute()) }

    // Permission Launcher for Android 13+ Notifications
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.setReminderEnabled(true)
            isReminderEnabled = true
            NotificationHelper.scheduleDailyReminder(context, reminderHour, reminderMinute)
        } else {
            viewModel.setReminderEnabled(false)
            isReminderEnabled = false
        }
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            reminderHour = hour
            reminderMinute = minute
            viewModel.setReminderTime(hour, minute)
            if (isReminderEnabled) {
                NotificationHelper.scheduleDailyReminder(context, hour, minute)
            }
        },
        reminderHour,
        reminderMinute,
        true // 24-hour view
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()
        ) {
            // Reminders Section
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Daily Study Reminder", style = MaterialTheme.typography.bodyLarge)
                    if (isReminderEnabled) {
                        Text(
                            text = String.format(Locale.getDefault(), "At %02d:%02d", reminderHour, reminderMinute),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isReminderEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@Switch
                            }
                        }
                        
                        isReminderEnabled = enabled
                        viewModel.setReminderEnabled(enabled)
                        if (enabled) {
                            NotificationHelper.createNotificationChannel(context)
                            NotificationHelper.scheduleDailyReminder(context, reminderHour, reminderMinute)
                        } else {
                            NotificationHelper.cancelDailyReminder(context)
                        }
                    }
                )
            }

            if (isReminderEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { timePickerDialog.show() }
                ) {
                    Text("Change Reminder Time")
                }
            }


        }
    }
}
