package com.example.weatherapplication.presentation


import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapplication.TAG
import com.example.weatherapplication.fontFamily
import com.example.weatherapplication.ui.theme.textColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    navController: NavController
) {

    val favUiState = favoritesViewModel.uiState.collectAsState().value
    val mainUiState = mainViewModel.uiState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()

    val favCities by favoritesViewModel.favoriteCities.collectAsState()

    val isSheetOpen = favUiState.isSheetOpen
    val sheetState = rememberModalBottomSheetState()

    val context = LocalContext.current
    LaunchedEffect(key1 = favoritesViewModel.showToastErrorChannel) {
        favoritesViewModel.showToastErrorChannel.collectLatest { show ->
            if (show) Toast.makeText(context, "Cannot Add More Than 10 Cities", Toast.LENGTH_SHORT)
                .show()
        }

    }

    if (isSheetOpen == true) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { favoritesViewModel.disableScreen() },
            containerColor = Color(0xFF265380),
            shape = AbsoluteRoundedCornerShape(size = 24.dp),
            modifier = Modifier
                .height(400.dp)

        ) {

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
                item {
                    Text(
                        text = "Favourite Cities",
                        color = textColor,
                        fontSize = 28.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }
                items(favCities.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable {
                                Log.d(
                                    TAG,
                                    " lat and long from favorites screen is" + favCities[index].latitude + " " + favCities[index].longitude
                                )
                                mainViewModel.setLatitudeLongitude(
                                    latitude = favCities[index].latitude,
                                    longitude = favCities[index].latitude
                                )
                                Log.d(
                                    TAG,
                                    "loading value in starting of fav " + mainUiState.isLoading
                                )

                                coroutineScope.launch {
                                    mainViewModel.getLocationData(
                                        latitude = favCities[index].latitude,
                                        longitude = favCities[index].longitude
                                    )
                                    delay(1000)
                                    mainViewModel.getCurrentData()
                                    mainViewModel.getHourlyData()
                                    mainViewModel.getDailyForecastData()

                                }

                            },
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
                            modifier = Modifier.clickable {
                                // search for the city
                            }
                        )
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

}
