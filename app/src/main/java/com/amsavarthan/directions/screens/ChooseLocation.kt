package com.amsavarthan.directions.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ChooseLocationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        CardField(
            label = "From",
            color = MaterialTheme.colors.primary,
            value = "Current Location"
        )
        Divider(color = MaterialTheme.colors.onBackground.copy(0.05f))
        CardField(label = "To", color = MaterialTheme.colors.secondary, value = "Riyaa's Home")
    }
}

@Preview
@Composable
private fun ScreenPreview() {
    ChooseLocationScreen(navController = rememberNavController())
}