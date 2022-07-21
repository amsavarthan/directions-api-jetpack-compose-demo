package com.amsavarthan.directions.model

import com.google.gson.annotations.SerializedName

data class Route(
    val legs: List<Legs>,
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline
)
