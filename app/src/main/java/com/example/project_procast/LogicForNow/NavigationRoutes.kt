package com.example.project_procast.LogicForNow

object AppRoutes {
    const val AppList = "app_list"
    const val SelectedApps = "selected_apps"
    const val AppDashboard = "app_dashboard/{packageName}"
    
    // Helper function to create the route with package name
    fun appDashboardRoute(packageName: String) = "app_dashboard/$packageName"
}
