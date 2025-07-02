package com.example.project_procast

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.tooling.preview.Preview
import com.example.project_procast.LogicForNow.SelectedAppsManager
import com.example.project_procast.ui.getUserInstalledApps
@Composable
fun SelectedAppsScreen(onNavigateToDashboard: (String) -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val coroutineScope = rememberCoroutineScope()

    var selectedPackages by remember { mutableStateOf<Set<String>>(emptySet()) }
    val allApps = remember { getUserInstalledApps(packageManager) }

    // Load selected packages
    LaunchedEffect(true) {
        SelectedAppsManager.getSelectedApps(context).collect {
            selectedPackages = it
        }
    }

    val selectedAppInfos = allApps.filter { it.packageName in selectedPackages }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Text("Tracked Apps", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(selectedAppInfos) { app ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable{
                            onNavigateToDashboard(app.packageName)
                        }
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = app.icon.toBitmap().asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(app.appName)
                }
            }
        }
    }
}
