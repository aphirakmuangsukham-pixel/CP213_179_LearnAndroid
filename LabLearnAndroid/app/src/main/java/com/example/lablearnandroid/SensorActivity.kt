package com.example.lablearnandroid

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class SensorActivity : ComponentActivity() {

    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            SensorScreen(viewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSensors()
    }
}

@Composable
fun SensorScreen(viewModel: SensorViewModel) {
    val context = LocalContext.current
    val accelData by viewModel.accelData.collectAsState()
    val locationData by viewModel.locationData.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.startSensors()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            viewModel.startSensors() // Accelerometer will still work
        }
    }

    LaunchedEffect(Unit) {
        val finePerm = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        if (finePerm != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            viewModel.startSensors()
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopSensors()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Accelerometer (m/s²)", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text("X: ${"%.2f".format(accelData.first)}")
        Text("Y: ${"%.2f".format(accelData.second)}")
        Text("Z: ${"%.2f".format(accelData.third)}")
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Location", fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        if (locationData != null) {
            Text("Latitude: ${locationData?.first}")
            Text("Longitude: ${locationData?.second}")
        } else {
            Text("Waiting for location...")
        }
    }
}
