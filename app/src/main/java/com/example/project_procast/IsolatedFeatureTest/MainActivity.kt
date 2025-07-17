package com.example.project_procast.IsolatedFeatureTest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private lateinit var postureDetector: PostureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postureDetector = PostureDetector(this)
        postureDetector.startDetection()
        setContent {
            MaterialTheme {
                PostureStatusScreen(postureDetector)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        postureDetector.stopDetection()
    }
}

@Composable
fun PostureStatusScreen(postureDetector: PostureDetector) {
    val posture by postureDetector.currentPosture.observeAsState(PostureDetector.PostureState.UNKNOWN)
    val (emoji, text) = when (posture) {
        PostureDetector.PostureState.STANDING -> "🧍" to "Standing"
        PostureDetector.PostureState.SITTING -> "🪑" to "Sitting"
        PostureDetector.PostureState.LYING_DOWN -> "🛏️" to "Lying Down"
        PostureDetector.PostureState.WALKING -> "🚶" to "Walking"
        else -> "❓" to "Unknown"
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text, fontSize = 32.sp)
        }
    }
} 