package com.example.weatherapplication.presentation

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.R
import com.example.weatherapplication.Screen
import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import com.example.weatherapplication.data.model.HourlyData.HourlyData
import com.example.weatherapplication.fontFamily
import com.example.weatherapplication.ui.theme.backgroundColor
import com.example.weatherapplication.ui.theme.border_color
import com.example.weatherapplication.ui.theme.textColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherApp(
    mainViewModel: MainViewModel = viewModel(), navController: NavController
) {

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("MMM d, yyyy    HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTime()
            delay(60000) // Update every minute
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        val mainUiState = mainViewModel.uiState.collectAsState().value

        val locationData = mainUiState.locationData
        val currentConditions = mainUiState.currentData
        val hourlyData = mainUiState.hourlyData
        val dailyForecast = mainUiState.dailyData


        val context = LocalContext.current
        LaunchedEffect(key1 = mainViewModel.showToastErrorChannel) {
            mainViewModel.showToastErrorChannel.collectLatest { show ->
                if (show) Toast.makeText(context, "sada", Toast.LENGTH_SHORT).show()
            }

        }


        if (locationData == null || currentConditions == null) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            val cityName =
                remember { mutableStateOf(locationData.LocalizedName + ",\n\n" + locationData.AdministrativeArea.LocalizedName + "\u25bd") }
            val bgImage = painterResource(id = R.drawable.bg_image)

            Scaffold(topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentTime,
                                fontSize = 20.sp,
                                color = Color.White,
                                textAlign = TextAlign.Start,
                                fontStyle = FontStyle.Italic,
                                fontFamily = fontFamily,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.FavoritesScreen.route) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "",
                                Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "log in icon",
                                Modifier.size(32.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .background(Color(0xFF265380))
                        .padding(8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1F4179))

                )
            }) { values ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Image(
                        painter = bgImage,
                        contentDescription = "background image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxHeight()
                            .background(backgroundColor.copy(alpha = 0.2f))
                            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CurrentWeather(currentConditions, cityName = cityName.value)

                        val pagerState = rememberPagerState(pageCount = { 2 })
                        val coroutineScope = rememberCoroutineScope()
                        TabRow(selectedTabIndex = pagerState.currentPage,
                            containerColor = backgroundColor.copy(alpha = 0.5f),
                            contentColor = Color.White,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    height = 2.dp,
                                    color = Color(0xFF87CEEB)
                                )
                            }) {
                            Tab(selected = pagerState.currentPage == 0, onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            }, text = {
                                Text(text = "Today")
                            })
                            Tab(selected = pagerState.currentPage == 1, onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            }, text = {
                                Text(text = "5 Day Weather Forecast")
                            })

                        }
                        HorizontalPager(
                            state = pagerState, userScrollEnabled = false
                        ) { page ->
                            if (page == 0) {
                                HourlyWeatherData(scrollState = rememberScrollState(), hourlyData)
                            } else {
                                DailyWeatherData(dailyForecast)
                            }

                        }


                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeather(currentConditions: CurrentConditions, cityName: String) {
    val degree =
        remember { mutableStateOf(currentConditions[0].Temperature.Metric.Value.toString() + "\u00b0C") }
    val minDegree =
        remember { mutableStateOf(currentConditions[0].TemperatureSummary.Past24HourRange.Minimum.Metric.Value.toString() + "\u00b0C") }
    val maxDegree =
        remember { mutableStateOf(currentConditions[0].TemperatureSummary.Past24HourRange.Maximum.Metric.Value.toString() + "\u00b0C") }
    val weatherText = remember { mutableStateOf(currentConditions[0].WeatherText) }

    Text(text = cityName,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 36.sp,
        fontFamily = fontFamily,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.W400,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            .height(125.dp)
            .border(2.dp, border_color, shape = AbsoluteRoundedCornerShape(24.dp))
            .background(
                color = backgroundColor.copy(alpha = 0.2f),
                shape = AbsoluteRoundedCornerShape(24.dp)
            )
            .clickable { })

    Column(
        modifier = Modifier
            .size(450.dp)
            .padding(top = 24.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            .border(2.dp, border_color, shape = AbsoluteRoundedCornerShape(24.dp))
            .background(
                color = backgroundColor.copy(alpha = 0.2f),
                shape = AbsoluteRoundedCornerShape(24.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sunny),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .size(150.dp)
                .padding(16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(
                text = degree.value,
                color = textColor,
                fontSize = 48.sp,
                fontStyle = FontStyle.Italic,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W300,
                modifier = Modifier.padding(start = 48.dp, end = 32.dp)

            )
            Column {
                Text(
                    text = "\u2191 " + maxDegree.value,
                    color = textColor,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.W200,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom=4.dp)
                )
                Text(
                    text = "\u2193 " + minDegree.value,
                    color = textColor,
                    fontWeight = FontWeight.W300,
                    fontSize = 20.sp
                )
            }

        }
        Text(
            text = weatherText.value,
            color = textColor,
            textAlign = TextAlign.Center,
            fontSize = 42.sp,
            fontFamily = fontFamily,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.W300,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_drop),
                        contentDescription = "droplet",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "12%",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W300,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_pressure),
                        contentDescription = "pressure",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "1000hpa",
                        color = textColor,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W300,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.wind),
                        contentDescription = "wind",
                        modifier = Modifier.size(32.dp),

                        )
                    Text(
                        text = "12km/h",
                        color = textColor,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W300,
                    )
                }
            }
        }
    }
}


@Composable
fun HourlyWeatherData(scrollState: ScrollState, hourlyData: List<HourlyData>?) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .height(200.dp)
                .fillMaxWidth()
                .padding(top = 4.dp)

        ) {
            for (i in 0..11) {

                val result by remember { mutableStateOf( hourlyData!![i].IconPhrase) }
                val iconResource = when(result) {
                    "Sunny" -> R.drawable.ic_sunny
                    "Cloudy" -> R.drawable.ic_cloudy
                    "Showers" -> R.drawable.ic_rainshower
                    "Rain" -> R.drawable.ic_rainy
                    "Thunderstorms" -> R.drawable.ic_thunder
                    "Snow" -> R.drawable.ic_snowy
                    "Partly Cloudy" -> R.drawable.ic_sunnycloudy
                    "Mostly Cloudy" -> R.drawable.ic_very_cloudy
                    else -> R.drawable.unknown

                }
                Column(modifier = Modifier
                    .clickable { }
                    .fillMaxSize()
                    .width(150.dp)
                    .padding(start = 4.dp, end = 4.dp)
                    .border(
                        width = 2.dp,
                        color = border_color,
                        shape = AbsoluteRoundedCornerShape(16.dp)
                    )
                    .background(
                        backgroundColor.copy(alpha = 0.2f),
                        shape = AbsoluteRoundedCornerShape(16.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = hourlyData!![i].DateTime.substring(startIndex = 10, endIndex = 15),
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W300,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(bottom = 4.dp)
                            .border(
                                width = 2.dp,
                                color = border_color,
                                shape = AbsoluteRoundedCornerShape(16.dp)
                            )
                    )
                    Image(
                        painter = painterResource(id = iconResource),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    )
                    Text(
                        text = hourlyData[i].Temperature.Value.toString()+"\u00b0C",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp,
                        fontFamily = fontFamily,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.W200,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 2.dp)
                    )
                    Row(
                        modifier = Modifier.padding(start = 36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_drop),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = hourlyData[i].PrecipitationProbability.toString(),
                            color = textColor,
                            fontSize = 28.sp,
                            fontFamily = fontFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.W200,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                        )
                    }

                }
            }
        }
    }

}

@Composable
fun DailyWeatherData(dailyForecast: DailyWeatherForecast?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(725.dp)
            .padding(top = 16.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        for (i in 1..5) {
            val day = LocalDateTime.parse(dailyForecast!!.DailyForecasts[i].Date).dayOfWeek

            val result by remember { mutableStateOf( dailyForecast.DailyForecasts[i].Day.IconPhrase) }
            val iconResource = when(result) {
                "Sunny" -> R.drawable.ic_sunny
                "Cloudy" -> R.drawable.ic_cloudy
                "Showers" -> R.drawable.ic_rainshower
                "Rain" -> R.drawable.ic_rainy
                "Thunderstorms" -> R.drawable.ic_thunder
                "Snow" -> R.drawable.ic_snowy
                "Partly Cloudy" -> R.drawable.ic_sunnycloudy
                "Mostly Cloudy" -> R.drawable.ic_very_cloudy
                else -> R.drawable.unknown

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .padding(4.dp)
                    .border(
                        4.dp,
                        color = border_color,
                        shape = AbsoluteRoundedCornerShape(16.dp)
                    )
                    .background(
                        color = backgroundColor.copy(alpha = 0.3f),
                        shape = AbsoluteRoundedCornerShape(16.dp)
                    )

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 8.dp, bottom = 12.dp, top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$day--",
                        color = textColor,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 26.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(text = dailyForecast.DailyForecasts[i].Day.ShortPhrase,
                        color = textColor,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .height(75.dp)
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_rainy),
                        contentDescription = "icon",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(50.dp)
                            .padding(top = 4.dp, start = 4.dp, end = 24.dp)
                    )
                    Text(
                        text = "${dailyForecast.DailyForecasts[i].Temperature.Maximum.Value} °",
                        color = Color(0xFFeb9534),
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "${dailyForecast.DailyForecasts[i].Temperature.Minimum.Value}°",
                        color = Color(0xFF3483eb),
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = iconResource),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                    Text(
                        text = "${dailyForecast.DailyForecasts[i].Day.PrecipitationProbability}%",
                        color = textColor,
                        fontFamily = fontFamily,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W300,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WeatherApp(navController = rememberNavController())
}
