package com.amsavarthan.directions.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.directions.R
import com.amsavarthan.directions.model.Result
import com.amsavarthan.directions.retrofit.ApiInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class LocationType {
    ORIGIN, DESTINATION
}

class MyViewModel(
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    var editingLocationType = LocationType.ORIGIN

    private val _currentLatLng = MutableStateFlow(LatLng(0.0, 0.0))
    private val _originPlace = MutableStateFlow<Place?>(null)
    private val _destinationPlace = MutableStateFlow<Place?>(null)
    private val _polyLineList = MutableStateFlow(emptyList<LatLng>())
    private val _distance = MutableStateFlow("")
    private val _duration = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)

    val currentLatLng = _currentLatLng.asStateFlow()
    val originPlace = _originPlace.asStateFlow()
    val destinationPlace = _destinationPlace.asStateFlow()
    val polyLineList = _polyLineList.asStateFlow()
    val distance = _distance.asStateFlow()
    val duration = _duration.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    @RequiresPermission(
        allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION],
    )
    fun getCurrentUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val currentLocation = LatLng(it.latitude, it.longitude)
            viewModelScope.launch {
                _currentLatLng.emit(currentLocation)
            }
        }
    }

    fun updatePlace(place: Place) {
        clearPolyLine()
        viewModelScope.launch {
            when (editingLocationType) {
                LocationType.ORIGIN -> _originPlace.emit(place)
                LocationType.DESTINATION -> _destinationPlace.emit(place)
            }
        }
    }

    fun getDirections(context: Context) {
        val api = ApiInterface.create()
        viewModelScope.launch { _isLoading.emit(true) }
        api.getDirection(
            "place_id:${originPlace.value?.id}",
            "place_id:${destinationPlace.value?.id}",
            context.getString(R.string.api_key)
        ).enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                viewModelScope.launch {
                    response.body()?.run {
                        val route = routes.firstOrNull()
                        val decodedPolyLine = PolyUtil.decode(
                            route?.overviewPolyline?.points ?: ""
                        )
                        val legs = route?.legs?.firstOrNull()
                        _distance.emit(legs?.distance?.text ?: "Not available")
                        _duration.emit(legs?.duration?.text ?: "Not available")
                        _polyLineList.emit(decodedPolyLine ?: emptyList())
                    }

                    _isLoading.emit(false)
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                viewModelScope.launch { _isLoading.emit(false) }
                Toast.makeText(context, "Error getting directions", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun clearPolyLine() {
        viewModelScope.launch { _polyLineList.emit(emptyList()) }
    }

    fun getPlacesLauncherIntent(context: Context): Intent {
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        return Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            fields
        ).setCountry("IN").build(context)
    }

    fun openSettings(context: Context) {
        val uri = Uri.fromParts("package", context.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = uri
        }
        context.startActivity(intent)
    }


}