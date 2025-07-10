package com.example.project_procast.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.project_procast.repository.UsageRepository
import com.example.project_procast.services.AppTrackingService
import com.example.project_procast.utils.hasUsageStatsPermission

@Composable
fun AppDashboardScreen(packageName: String) {
    val context = LocalContext.current
    val hasPermission = hasUsageStatsPermission(context)
    
    // Get launch count from repository
    val usageRepository = remember { UsageRepository(context) }
    val launchCount = remember(packageName) { 
        usageRepository.getLaunchCount(packageName) 
    }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Usage Access Permission Needed")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }) {
                Text("Grant Permission")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Dashboard for: $packageName", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("App usage data will go here")
        Text("Times opened today: $launchCount")
  
    }
}