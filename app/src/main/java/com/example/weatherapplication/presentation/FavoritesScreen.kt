package com.example.weatherapplication.presentation


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapplication.Screen
import com.example.weatherapplication.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    onClose: () -> Unit,
    navController: NavController
) {

    val coroutineScope = rememberCoroutineScope()

    val favCities by favoritesViewModel.favoriteCities.collectAsState()



    /*val context = LocalContext.current
    LaunchedEffect(key1 = favoritesViewModel.showToastErrorChannel) {
        favoritesViewModel.showToastErrorChannel.collectLatest { show ->
            if (show) Toast.makeText(context, "Cannot Add More Than 10 Cities", Toast.LENGTH_SHORT)
                .show()
        }

    }*/

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f) // Set the height to 70%
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TopAppBar(
            title = { Text("Favorite Cities") },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            },

        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFa195c3),
                            Color(0xFF7baada)
                        )
                    )
                )
                .padding(bottom = 30.dp)
        ) {
            items(favCities.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = favCities[index].cityName,
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W400,

                        )
                    IconButton(onClick = {
                        mainViewModel.setLatitudeLongitude(
                            latitude = favCities[index].latitude,
                            longitude = favCities[index].latitude
                        )

                        coroutineScope.launch {
                            mainViewModel.getLocationData(
                                latitude = favCities[index].latitude,
                                longitude = favCities[index].longitude
                            )
                            mainViewModel.getCurrentData()
                            mainViewModel.getHourlyData()
                            mainViewModel.getDailyForecastData()
                            onClose()
                            navController.navigate(Screen.MainScreen.route)
                        }

                    }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            Modifier.size(32.dp),
                            tint = Color.Black
                        )

                    }
                    IconButton(
                        onClick = { favoritesViewModel.deleteFavCity(favCities[index].cityName) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Black,
                            modifier = Modifier.size(35.dp)

                        )
                    }


                }

            }

        }
    }

}
