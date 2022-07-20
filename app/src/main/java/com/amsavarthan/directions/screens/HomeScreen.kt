package com.amsavarthan.directions.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveEta
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.amsavarthan.directions.R
import com.amsavarthan.directions.Screen

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {

    val from by remember {
        mutableStateOf("")
    }

    val to by remember {
        mutableStateOf("")
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.DriveEta,
                    contentDescription = "Application Logo",
                    tint = MaterialTheme.colors.onBackground.copy(0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colors.onBackground.copy(
                        0.4f
                    ),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column {
                Text(
                    text = "Select journey route",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colors.onBackground.copy(
                        0.4f
                    ),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    onClick = { navController.navigate(Screen.ChooseLocation.route) },
                    shape = RoundedCornerShape(4),
                    elevation = 24.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        CardField(
                            label = "From",
                            color = MaterialTheme.colors.primary,
                            value = from
                        )
                        Divider(color = MaterialTheme.colors.onBackground.copy(0.05f))
                        CardField(label = "To", color = MaterialTheme.colors.secondary, value = to)
                    }
                }
            }
        }
    }
}

@Composable
fun CardField(label: String, color: Color, value: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .padding(horizontal = 18.dp),
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
                .padding(top = 4.dp, start = 34.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            if (value.isNotBlank()) {
                Text(text = value, style = MaterialTheme.typography.h6)
            }
        }
    }
}

@Preview
@Composable
private fun ScreenPreview() {
    HomeScreen(navController = rememberNavController())
}