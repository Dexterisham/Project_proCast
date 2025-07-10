package com.example.project_procast.services

import android.app.AppOpsManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context
import android.util.Log
import com.example.project_procast.LogicForNow.SelectedAppsManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class AppTrackingService : Service() {
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var usageStatsManager: UsageStatsManager
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        Log.d("AppTrackingService", "Service created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppTrackingService", "Service started")
        
        // Check permission first
        if (!hasUsageStatsPermission()) {
            Log.e("AppTrackingService", "No usage stats permission")
            stopSelf()
            return START_NOT_STICKY
        }
        
        startForegroundDetection()
        return START_STICKY
    }
    
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    private fun startForegroundDetection() {
        serviceScope.launch {
            Log.d("AppTrackingService", "Starting foreground detection")
            while (true) {
                checkForegroundApps()
                delay(2000) // Check every 2 seconds
            }
        }
    }
    
    private suspend fun checkForegroundApps() {
        try {
            Log.d("AppTrackingService", "Checking foreground apps...")
            
            // Get selected apps
            val selectedApps = SelectedAppsManager.getSelectedApps(this).first()
            Log.d("AppTrackingService", "Selected apps: $selectedApps")
            
            // Get current foreground app
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 2000 // Last 2 seconds
            
            val events = usageStatsManager.queryEvents(startTime, endTime)
            var eventCount = 0
            
            while (events.hasNextEvent()) {
                val event = UsageEvents.Event()
                events.getNextEvent(event)
                eventCount++
                
                if (event.packageName in selectedApps) {
                    when (event.eventType) {
                        UsageEvents.Event.MOVE_TO_FOREGROUND -> {
                            Log.d("AppTrackingService", "🎯 Tracked app came to foreground: ${event.packageName}")
                        }
                        UsageEvents.Event.MOVE_TO_BACKGROUND -> {
                            Log.d("AppTrackingService", "🔚 Tracked app went to background: ${event.packageName}")
                        }
                    }
                }
            }
            
            Log.d("AppTrackingService", "Checked $eventCount events")
            
        } catch (e: Exception) {
            Log.e("AppTrackingService", "Error checking foreground apps: ${e.message}")
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("AppTrackingService", "Service destroyed")
    }
}