package com.example.weatherapplication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapplication.Screen
import com.example.weatherapplication.fontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(
    mainViewModel: MainViewModel = viewModel(),
    navController: NavController

) {

    val mainUiState = mainViewModel.uiState.collectAsState().value
    val searchResults = mainUiState.searchResult
    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = { newQuery ->
                query = newQuery.trim()
                mainViewModel.searchProductsWithDelay(newQuery)
            },
            label = { Text("Search for a city") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (searchResults != null) {
                items(searchResults.size) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${searchResults[it].LocalizedName}, ${searchResults[it].AdministrativeArea.LocalizedName}",
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Start,
                            fontStyle = FontStyle.Italic,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.W400,
                        )
                        IconButton(onClick = {
                            coroutineScope.launch {
                                mainViewModel.getLocationDataWithLocationKey(locationKey = searchResults[it].Key)
                                mainViewModel.getCurrentData()
                                mainViewModel.getHourlyData()
                                mainViewModel.getDailyForecastData()
                                withContext(Dispatchers.Main){
                                    navController.navigate(Screen.MainScreen.route)
                                }
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


                    }

                }
            }


        }
    }
}