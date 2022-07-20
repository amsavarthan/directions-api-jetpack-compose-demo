package com.amsavarthan.directions

sealed class Screen(val route:String){
    object Home:Screen("home")
    object ChooseLocation:Screen("choose_location")
    object ShowDirection:Screen("show_direction")
}
