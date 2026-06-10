package com.app.quran.ui.navigation

sealed class Screen(val route: String) {
    object Player : Screen("player")
    object Library : Screen("library")
    object Bookmarks : Screen("bookmarks")
    object Readers : Screen("readers")
    object Settings : Screen("settings")
}