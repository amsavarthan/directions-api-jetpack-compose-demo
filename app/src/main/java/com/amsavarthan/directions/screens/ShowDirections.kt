package com.amsavarthan.directions.screens

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ShowDirectionsScreen(navController: NavController) {
    Button(onClick = { navController.popBackStack() }) {
        Text(text = "Go back")
    }
}