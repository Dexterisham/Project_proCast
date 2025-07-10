package com.example.project_procast

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.project_procast.LogicForNow.AppRoutes
import com.example.project_procast.ui.AppDashboardScreen
import com.example.project_procast.ui.AppListScreen
import com.example.project_procast.ui.theme.Project_ProcastTheme
import com.example.project_procast.services.AppTrackingService

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
                    SelectedAppsScreen(
                    onNavigateToDashboard={packageName ->
                        navController.navigate(AppRoutes.appDashboardRoute(packageName))
                    }
                    )
                }
                composable(
                    route=AppRoutes.AppDashboard, 
                    arguments = listOf(navArgument("packageName") 
                { type = NavType.StringType })
                ) 
                { backStackEntry ->
                    val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
                    AppDashboardScreen(packageName = packageName)
                }
            }
        }
        startService(Intent(this, AppTrackingService::class.java))
    }
}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Project_ProcastTheme {
//        AppListScreen()
//    }
//}