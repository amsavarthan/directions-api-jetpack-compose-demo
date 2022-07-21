package com.amsavarthan.directions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.amsavarthan.directions.ui.theme.DirectionsTheme
import com.amsavarthan.directions.viewmodel.MyViewModel
import com.amsavarthan.directions.viewmodel.MyViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<MyViewModel> { MyViewModelFactory(fusedLocationClient) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DirectionsTheme {
                HomeScreen(viewModel)
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }


}
