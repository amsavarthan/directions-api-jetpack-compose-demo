package com.amsavarthan.directions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationDisabled
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amsavarthan.directions.ui.theme.Black
import com.amsavarthan.directions.viewmodel.LocationType
import com.amsavarthan.directions.viewmodel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(viewModel: MyViewModel = viewModel()) {

    val locationPermissionsLauncher = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { permissionStateMap ->
        if (permissionStateMap.containsValue(false))
            return@rememberMultiplePermissionsState
        //all permission granted
        viewModel.getCurrentUserLocation()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    locationPermissionsLauncher.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    if (locationPermissionsLauncher.allPermissionsGranted) {
        PermissionGrantedView(viewModel)
    } else {
        PermissionMissingView(viewModel, locationPermissionsLauncher)
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PermissionGrantedView(
    viewModel: MyViewModel,
) {

    val context = LocalContext.current
    val polyLineList by viewModel.polyLineList.collectAsState()
    val currentLatLng by viewModel.currentLatLng.collectAsState()
    val originPlace by viewModel.originPlace.collectAsState()
    val destinationPlace by viewModel.destinationPlace.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val distance by viewModel.distance.collectAsState()

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
            )
        )
    }
    val properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true,
            )
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            currentLatLng, 18f
        )
    }

    LaunchedEffect(currentLatLng) {
        if (polyLineList.isNotEmpty()) return@LaunchedEffect
        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
    }
    LaunchedEffect(polyLineList) {
        if (polyLineList.isEmpty()) return@LaunchedEffect
        val bounds = LatLngBounds.builder()
            .include(originPlace?.latLng!!)
            .include(destinationPlace?.latLng!!)
            .build()
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    val autoCompletePlacesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> result.data?.let {
                val place = Autocomplete.getPlaceFromIntent(it)
                viewModel.updatePlace(place)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        //Map View

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .weight(1F),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
        ) {

            if (polyLineList.isNotEmpty()) {
                originPlace?.latLng?.let {
                    Marker(
                        state = MarkerState(it),
                        title = originPlace?.name
                    )
                }
                destinationPlace?.latLng?.let {
                    Marker(
                        state = MarkerState(it),
                        title = destinationPlace?.name
                    )
                }
                Polyline(points = polyLineList, width = 12f)
            }

        }

        //Details Card

        Card(
            shape = RoundedCornerShape(0),
            elevation = 24.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(targetState = polyLineList.isNotEmpty()) { polyLineDrawn ->
                if (polyLineDrawn) {
                    RouteInfo(distance = distance, duration = duration)
                } else {
                    Column {
                        CardField(
                            label = "From",
                            color = MaterialTheme.colors.primary,
                            value = originPlace?.name.orEmpty(),
                            onClick = {
                                if (isLoading) return@CardField
                                viewModel.editingLocationType = LocationType.ORIGIN
                                autoCompletePlacesLauncher.launch(
                                    viewModel.getPlacesLauncherIntent(
                                        context
                                    )
                                )
                            }
                        )
                        Divider(color = MaterialTheme.colors.onBackground.copy(0.05f))
                        CardField(
                            label = "To",
                            color = MaterialTheme.colors.secondary,
                            value = destinationPlace?.name.orEmpty(),
                            onClick = {
                                if (isLoading) return@CardField
                                viewModel.editingLocationType = LocationType.DESTINATION
                                autoCompletePlacesLauncher.launch(
                                    viewModel.getPlacesLauncherIntent(
                                        context
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }

        //Button

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isLoading.not()) {
                    when {
                        polyLineList.isNotEmpty() -> viewModel.clearPolyLine()
                        originPlace != null && destinationPlace != null ->
                            viewModel.getDirections(context)
                    }

                }
                .background(Black)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    text = if (polyLineList.isEmpty()) "Get Best Route" else "Back",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CardField(
    label: String,
    color: Color,
    value: String,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(color, CircleShape)
                    .size(8.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.4f)
            )
        }
        Row(
            Modifier
                .padding(top = 4.dp, start = 32.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            if (value.isNotBlank()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.h6,
                )
            }
        }
    }
}

@Composable
private fun RouteInfo(distance: String, duration: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(24.dp)
    ) {
        RouteInfoItem(
            label = "Distance",
            value = distance,
            icon = Icons.Outlined.PinDrop
        )
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )
        RouteInfoItem(
            label = "Duration",
            value = duration,
            icon = Icons.Outlined.Schedule
        )
    }
}

@Composable
private fun RowScope.RouteInfoItem(label: String, icon: ImageVector, value: String) {
    Row(
        modifier = Modifier.weight(1f),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
    }
}


//Missing Permission Views

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionMissingView(
    viewModel: MyViewModel,
    locationPermissionsLauncher: MultiplePermissionsState
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationDisabled,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(text = "Missing Permissions", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Location permission is missing",
                style = MaterialTheme.typography.subtitle1
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (locationPermissionsLauncher.allPermissionsGranted) return@clickable
                    if (locationPermissionsLauncher.shouldShowRationale && locationPermissionsLauncher.revokedPermissions.isNotEmpty()) {
                        locationPermissionsLauncher.launchMultiplePermissionRequest()
                        return@clickable
                    }
                    viewModel.openSettings(context)
                }
                .background(Black)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Grant Permissions",
                color = Color.White
            )
        }
    }
}