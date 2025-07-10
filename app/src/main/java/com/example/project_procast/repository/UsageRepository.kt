package com.example.project_procast.repository

import android.app.usage.UsageStatsManager
import android.app.usage.UsageEvents
import android.content.Context

class UsageRepository(private val context: Context) {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    
    fun getLaunchCount(packageName: String): Int {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        
        val events = usageStatsManager.queryEvents(startOfDay, now)
        var launchCount = 0

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)
            if (event.packageName == packageName &&
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                launchCount++
            }
        }
        
        return launchCount
    }
}
