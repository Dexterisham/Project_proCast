package com.example.project_procast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_procast.LogicForNow.AppRoutes
import com.example.project_procast.ui.AppListScreen
import com.example.project_procast.ui.theme.Project_ProcastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = AppRoutes.AppList) {
                composable(AppRoutes.AppList) {
                    AppListScreen(
                        onNavigateToSelected = {
                            navController.navigate(AppRoutes.SelectedApps)
                        }
                    )
                }

                composable(AppRoutes.SelectedApps) {
                    SelectedAppsScreen()
                }
            }
        }

    }
}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Project_ProcastTheme {
//        AppListScreen()
//    }
//}