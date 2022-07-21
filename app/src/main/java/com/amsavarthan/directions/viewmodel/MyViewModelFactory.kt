package com.amsavarthan.directions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient

class MyViewModelFactory(private val fusedLocationClient: FusedLocationProviderClient) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModel::class.java)) {
            return MyViewModel(fusedLocationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}