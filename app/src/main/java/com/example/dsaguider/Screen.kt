package com.example.dsaguider
// sealed so that no one can inherit from them -> a // Or whatever package you've placed it in
sealed  class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object AddScreen: Screen("add_screen") // Make sure this exists
    // Add other screens here
}
