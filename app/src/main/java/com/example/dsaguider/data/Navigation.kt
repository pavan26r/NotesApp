package com.example.dsaguider.data

import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dsaguider.HomeView
import com.example.dsaguider.Screen
import java.util.Map.entry

@Composable
fun Navigation(
    viewModel: WishViewModel = viewModel(),
    // It's common to use NavHostController as the type for the controller
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            // Pass navController and viewModel to HomeView
            // if HomeView needs to navigate or access data.
            HomeView(navController, viewModel)
        }
        //second screen
        composable(Screen.AddScreen.route + "/{id}", arguments = listOf(
            navArgument("id") {
                type = NavType.LongType
                defaultValue = 0L
                nullable = false
            }
        ))


            { entry ->
                val id = if(entry.arguments != null) entry!!.arguments!!.getLong("id") else 0L
            AddEditDetailView(id = id, viewModel = viewModel, navController = navController)

        }

}
}