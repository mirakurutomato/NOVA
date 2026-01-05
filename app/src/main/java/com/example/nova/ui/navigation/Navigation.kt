package com.example.nova.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nova.ui.screens.HomeScreen // Restored
// import com.example.nova.ui.screens.MemoryScreen // Removed
import com.example.nova.ui.screens.SettingsScreen
import com.example.nova.ui.screens.VideoScreen

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.nova.R
import com.example.nova.ui.theme.glass

// Define Routes
sealed class Screen(val route: String, @StringRes val titleResId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.nav_home, Icons.Filled.Home) // Restored (and now contains Memory UI)
    object Video : Screen("video", R.string.nav_video, Icons.Filled.Videocam)
    // object Memory : Screen("memory", R.string.nav_memory, Icons.Filled.History) // Removed
    object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    videoRendererComposable: @Composable () -> Unit // Pass the GLSurfaceView as a composable lambda
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route, // Restored Home as start
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen() } // Now Memory UI
        composable(Screen.Video.route) {
            VideoScreen(videoRendererComposable = videoRendererComposable)
        }
        // composable(Screen.Memory.route) { MemoryScreen() } // Removed
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Colors: Modern White/Blue scheme
    val barColor = Color.White
    val accentBlue = Color(0xFF1565C0) // Material Blue 800
    val lightBlue = Color(0xFF42A5F5) // Material Blue 400
    val unselectedGray = Color(0xFF9E9E9E)

    // Container: Full width, attached to bottom
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp), // Taller to accommodate elevated button
        contentAlignment = Alignment.BottomCenter
    ) {
        // White Background Bar (flat, no shadow to avoid gap)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter),
            shape = RectangleShape,
            color = barColor
            // No shadowElevation - eliminates the gap/strip
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Item: Home
                NavItem(
                    screen = Screen.Home,
                    isSelected = currentRoute == Screen.Home.route,
                    selectedColor = accentBlue,
                    unselectedColor = unselectedGray,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                
                // Center Spacer (for the elevated button)
                Spacer(modifier = Modifier.width(72.dp))

                // Right Item: Settings
                NavItem(
                    screen = Screen.Settings,
                    isSelected = currentRoute == Screen.Settings.route,
                    selectedColor = accentBlue,
                    unselectedColor = unselectedGray,
                    onClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

        // Center Elevated Button (Video/Camera) - Sits ON TOP of the bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (currentRoute == Screen.Video.route) accentBlue else lightBlue,
                        shape = CircleShape
                    )
                    .border(3.dp, Color.White, CircleShape)
                    .clickable {
                        navController.navigate(Screen.Video.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = stringResource(Screen.Video.titleResId),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = stringResource(Screen.Video.titleResId),
                style = MaterialTheme.typography.labelSmall,
                color = if (currentRoute == Screen.Video.route) accentBlue else unselectedGray
            )
        }
    }
}

@Composable
fun NavItem(
    screen: Screen, 
    isSelected: Boolean, 
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.titleResId),
            tint = if (isSelected) selectedColor else unselectedColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(screen.titleResId),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) selectedColor else unselectedColor
        )
    }
}
