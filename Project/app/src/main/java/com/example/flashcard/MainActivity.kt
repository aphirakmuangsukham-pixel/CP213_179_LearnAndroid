package com.example.flashcard

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.flashcard.data.local.SettingsRepository
import com.example.flashcard.ui.AppNavigation

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        setContent {
            com.example.flashcard.ui.theme.FlashCardTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Enable Focus Mode if settings are turned on
        if (settingsRepository.isFocusModeEnabled() && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }

    override fun onStop() {
        super.onStop()
        // Always turn off Focus Mode when app goes to the background
        if (settingsRepository.isFocusModeEnabled() && notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}
