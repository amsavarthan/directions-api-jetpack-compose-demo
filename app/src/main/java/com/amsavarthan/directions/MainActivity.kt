package com.amsavarthan.directions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.directions.screens.ChooseLocationScreen
import com.amsavarthan.directions.screens.HomeScreen
import com.amsavarthan.directions.screens.ShowDirectionsScreen
import com.amsavarthan.directions.ui.theme.DirectionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    DirectionsTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.ChooseLocation.route) {
                ChooseLocationScreen(navController)
            }
            composable(Screen.ShowDirection.route) {
                ShowDirectionsScreen(navController)
            }
        }
    }
}