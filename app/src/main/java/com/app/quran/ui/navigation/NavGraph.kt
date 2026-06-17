package com.app.quran.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.quran.presentation.bookmarks.BookmarksScreen
import com.app.quran.presentation.library.LibraryScreen
import com.app.quran.presentation.player.PlayerScreen
import com.app.quran.presentation.readers.ReadersScreen
import com.app.quran.presentation.settings.SettingsScreen

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Player, Icons.Default.PlayCircle, "الرئيسية"),
    BottomNavItem(Screen.Library, Icons.Default.AutoStories, "المكتبة"),
    BottomNavItem(Screen.Bookmarks, Icons.Default.Bookmark, "العلامات"),
    BottomNavItem(Screen.Readers, Icons.Default.Person, "المشايخ"),
    BottomNavItem(Screen.Settings, Icons.Default.Settings, "الإعدادات")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "مصحف البلوثوث",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                        onClick = {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Player.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Player.route) {
                PlayerScreen()
            }
            composable(Screen.Library.route) {
                LibraryScreen()
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen()
            }
            composable(Screen.Readers.route) {
                ReadersScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}