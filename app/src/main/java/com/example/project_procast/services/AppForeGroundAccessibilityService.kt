package com.example.project_procast.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class AppForeGroundAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName.toString()
            Log.d("AppForeGroundAccessibilityService", "Package name: $packageName")
        }
    }

    override fun onInterrupt() {
        Log.d("AppForeGroundAccessibilityService", "Service interrupted")
    }
}