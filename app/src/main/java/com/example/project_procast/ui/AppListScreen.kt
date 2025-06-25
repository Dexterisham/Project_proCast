package com.example.project_procast.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.project_procast.LogicForNow.SelectedAppsManager
import kotlinx.coroutines.launch

// Data class to hold app info
data class AppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable
)

@Composable
fun AppListScreen(onNavigateToSelected: () -> Unit) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val coroutineScope = rememberCoroutineScope()

    // Get installed apps once
    val installedApps = remember {
        getUserInstalledApps(packageManager)
    }

    // Load selected apps from DataStore
    val selectedApps = remember { mutableStateListOf<String>() }

    // Collect selected apps from DataStore when screen starts
    LaunchedEffect(true) {
        SelectedAppsManager.getSelectedApps(context).collect { savedSet ->
            selectedApps.clear()
            selectedApps.addAll(savedSet)
            Log.d("SelectedApps", "Currently saved apps: $savedSet")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(horizontal = 16.dp)
    ) {
        Text("Select apps to track", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // 👇 Use weight(1f) so the list takes all available height minus the button
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(installedApps) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Image(
                            bitmap = app.icon.toBitmap().asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )
                        Text(app.appName)
                    }
                    Switch(
                        checked = app.packageName in selectedApps,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                selectedApps.add(app.packageName)
                            } else {
                                selectedApps.remove(app.packageName)
                            }

                            // Save the updated set
                            coroutineScope.launch {
                                SelectedAppsManager.saveSelectedApps(context, selectedApps.toSet())
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 👇 This will now always be visible under the list
        Button(
            onClick = onNavigateToSelected,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Go to Tracked Apps")
        }
    }

}


// Function to get list of user-installed apps
fun getUserInstalledApps(pm: PackageManager): List<AppInfo> {
    val launchableApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        .filter {
            // Only include apps with a launch intent (i.e., apps the user can open)
            pm.getLaunchIntentForPackage(it.packageName) != null &&
                    (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 // filter out system apps
        }
        .map {
            AppInfo(
                appName = pm.getApplicationLabel(it).toString(),
                packageName = it.packageName,
                icon = pm.getApplicationIcon(it)
            )
        }

    return launchableApps.sortedBy { it.appName.lowercase() }
}

