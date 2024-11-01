package com.example.weatherapplication.presentation

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.R
import com.example.weatherapplication.Screen
import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.fontFamily
import com.example.weatherapplication.ui.theme.Purple80
import com.example.weatherapplication.ui.theme.backgroundColor
import com.example.weatherapplication.ui.theme.border_color
import com.example.weatherapplication.ui.theme.textColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale


@Composable
fun WeatherApp(
    mainViewModel: MainViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    navController: NavController,
) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("MMM d, yyyy    HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    val currentTime = remember { mutableStateOf(getCurrentTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = getCurrentTime()
            delay(60000)
        }
    }


    val currentContext by rememberUpdatedState(newValue = context)
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            favoritesViewModel.toastMessage.asFlow().collect { message ->
                message?.let {
                    Toast.makeText(currentContext, it, Toast.LENGTH_SHORT).show()
                    favoritesViewModel.clearToastMessage()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            mainViewModel.toastMessage.asFlow().collect { message ->
                message?.let {
                    Toast.makeText(currentContext, it, Toast.LENGTH_LONG).show()
                    mainViewModel.clearToastMessage()
                }

            }

        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        WeatherScreen(
            navController = navController,
            currentTime = currentTime.value,
            mainViewModel = mainViewModel,
            favoritesViewModel = favoritesViewModel
        )
    }

}


@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun WeatherScreen(
    mainViewModel: MainViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel = viewModel(),
    navController: NavController,
    currentTime: String
) {

    val mainUiState = mainViewModel.uiState.collectAsState().value

    if (mainUiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val coroutineScope = rememberCoroutineScope()

        val locationData = mainUiState.locationData
        val currentConditions = mainUiState.currentData

        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                FavoritesScreen(
                    mainViewModel = mainViewModel,
                    onClose = { coroutineScope.launch { sheetState.hide() } },
                    navController = navController
                )
            }
        ) {

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
                        IconButton(onClick = { coroutineScope.launch { sheetState.show() } }) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "",
                                Modifier.size(32.dp),
                                tint = Purple80
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.LoginScreen.route) }) {
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
            }, floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val cityname = (locationData?.LocalizedName
                                ) + ", " + (locationData?.AdministrativeArea?.LocalizedName)
                        favoritesViewModel.addFavCity(
                            cityName = cityname,
                            latitude = mainUiState.locationData?.GeoPosition?.Latitude.toString(),
                            longitude = mainUiState.locationData?.GeoPosition?.Longitude.toString()
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add favourite City"
                    )
                }
            }
            ) { values ->
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
                            .fillMaxSize()
                            .background(backgroundColor.copy(alpha = 0.1f))
                            .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        CurrentWeather(
                            navController = navController,
                            locationData = locationData,
                            currentConditions = currentConditions
                        )


                        val pagerState = rememberPagerState(pageCount = { 2 })
                        TabRow(selectedTabIndex = pagerState.currentPage,
                            containerColor = backgroundColor.copy(alpha = 0.5f),
                            contentColor = Color.White,
                            modifier = Modifier.padding(top = 16.dp),
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                    height = 4.dp,
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
                                HourlyWeatherData(
                                    scrollState = rememberScrollState(),
                                    mainViewModel = mainViewModel
                                )
                            } else {
                                DailyWeatherData(mainViewModel)
                            }

                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CurrentWeather(
    navController: NavController,
    locationData: LocationData?,
    currentConditions: CurrentConditions?
) {

    val cityName =
        remember {
            mutableStateOf(
                (locationData?.LocalizedName) + ",\n\n" + (locationData?.AdministrativeArea?.LocalizedName) + "\u25bd"
            )
        }

    val degree =
        remember { mutableStateOf(currentConditions?.get(0)?.Temperature?.Metric?.Value.toString() + "\u00b0C") }
    val minDegree =
        remember { mutableStateOf(currentConditions?.get(0)?.TemperatureSummary?.Past24HourRange?.Minimum?.Metric?.Value.toString() + "\u00b0C") }
    val maxDegree =
        remember { mutableStateOf(currentConditions?.get(0)?.TemperatureSummary?.Past24HourRange?.Maximum?.Metric?.Value.toString() + "\u00b0C") }
    val weatherText = remember { mutableStateOf(currentConditions?.get(0)?.WeatherText ?: "") }

    val realFeelTemperature = remember {
        mutableStateOf(currentConditions?.get(0)?.RealFeelTemperature?.Metric?.Value.toString() + "\u00b0C")
    }
    val pressure = remember {
        mutableStateOf(
            (currentConditions?.get(0)?.Pressure?.Metric?.Value).toString() + " " + (currentConditions?.get(
                0
            )?.Pressure?.Metric?.Unit
                    )
        )
    }
    val wind = remember {
        mutableStateOf(
            (currentConditions?.get(0)?.Wind?.Speed?.Metric?.Value
                    ).toString() + " " + (currentConditions?.get(0)?.Wind?.Speed?.Metric?.Unit
                    )
        )
    }

    val result by remember {
        mutableIntStateOf(
            currentConditions?.get(0)?.WeatherIcon ?: 0
        )
    }

    val iconResource = when (result) {
        1, 33 -> R.drawable.sunny
        2, 3, 34, 35 -> R.drawable.partlysunny
        6, 7, 8, 38 -> R.drawable.cloudy
        4, 36 -> R.drawable.intermittent_clouds
        11 -> R.drawable.fog
        12, 13, 14, 39, 40 -> R.drawable.showers
        15, 16, 17, 41, 42 -> R.drawable.thunderstorms
        18 -> R.drawable.rain
        19, 20, 21, 22, 23, 43, 44 -> R.drawable.snow
        24, 25, 26 -> R.drawable.ice
        29 -> R.drawable.rainandsnow
        30 -> R.drawable.hot
        31 -> R.drawable.cold
        32 -> R.drawable.windy
        else -> R.drawable.unknown

    }

    Text(text = cityName.value,
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
                color = backgroundColor.copy(alpha = 0.3f),
                shape = AbsoluteRoundedCornerShape(24.dp)
            )
            .clickable { navController.navigate(Screen.SearchScreen.route) }
    )

    Column(
        modifier = Modifier
            .size(525.dp)
            .padding(top = 24.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            .border(2.dp, border_color, shape = AbsoluteRoundedCornerShape(24.dp))
            .background(
                color = backgroundColor.copy(alpha = 0.1f),
                shape = AbsoluteRoundedCornerShape(24.dp)
            )
            .verticalScroll(rememberScrollState())
    ) {

        Image(
            painter = painterResource(id = iconResource),
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
                Row(
                    modifier = Modifier.fillMaxWidth(
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2191 ", color = textColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = maxDegree.value,
                        color = textColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W200,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2193 ",
                        color = textColor,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = minDegree.value,
                        color = textColor,
                        fontWeight = FontWeight.W300,
                        fontSize = 20.sp
                    )
                }

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
                .padding(top = 10.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, bottom = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.temperature_feels_like),
                    contentDescription = "temperature_feels_like",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp)
                )
                Text(
                    text = realFeelTemperature.value,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontFamily = fontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.W300,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pressure),
                    contentDescription = "pressure",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp)
                )
                Text(
                    text = pressure.value,
                    color = textColor,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = fontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.W300,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.windsock),
                    contentDescription = "wind",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                )
                Text(
                    text = wind.value,
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


@Composable
fun HourlyWeatherData(mainViewModel: MainViewModel = viewModel(), scrollState: ScrollState) {

    val mainUiState = mainViewModel.uiState.collectAsState().value
    val hourlyData = mainUiState.hourlyData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .height(200.dp)
                .fillMaxWidth()
        ) {
            for (i in 0..11) {

                val result = hourlyData?.get(i)?.WeatherIcon
                val iconResource = when (result) {
                    1, 33 -> R.drawable.sunny
                    2, 3, 34, 35 -> R.drawable.partlysunny
                    6, 7, 8, 38 -> R.drawable.cloudy
                    4, 36 -> R.drawable.intermittent_clouds
                    11 -> R.drawable.fog
                    12, 13, 14, 39, 40 -> R.drawable.showers
                    15, 16, 17, 41, 42 -> R.drawable.thunderstorms
                    18 -> R.drawable.rain
                    19, 20, 21, 22, 23, 43, 44 -> R.drawable.snow
                    24, 25, 26 -> R.drawable.ice
                    29 -> R.drawable.rainandsnow
                    30 -> R.drawable.hot
                    31 -> R.drawable.cold
                    32 -> R.drawable.windy
                    else -> R.drawable.unknown

                }
                val date by remember {
                    mutableStateOf(
                        hourlyData?.get(i)?.DateTime?.substring(startIndex = 11, endIndex = 16)
                            ?: " "
                    )
                }

                val temparature = hourlyData?.get(i)?.Temperature?.Value.toString() + "\u00b0C"
                val precipitationProbability =
                    hourlyData?.get(i)?.PrecipitationProbability?.toString() + "%"

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
                        backgroundColor.copy(alpha = 0.1f),
                        shape = AbsoluteRoundedCornerShape(16.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = date,
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
                        text = temparature,
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
                        modifier = Modifier.padding(start = 24.dp, top = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_drop),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = precipitationProbability,
                            color = textColor,
                            fontSize = 22.sp,
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
fun DailyWeatherData(mainViewModel: MainViewModel = viewModel()) {

    val mainUiState = mainViewModel.uiState.collectAsState().value
    val dailyForecast = mainUiState.dailyData
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(725.dp)
            .padding(top = 16.dp, bottom = 28.dp)
            .background(
                color = backgroundColor.copy(alpha = 0.2f),
                shape = AbsoluteRoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val zoneId = ZoneId.systemDefault()
        val zdt = ZonedDateTime.now(zoneId)
        val zoneOffset = zdt.offset
        for (i in 0..4) {

            val day by remember {
                mutableStateOf(
                    LocalDateTime.ofEpochSecond(
                        dailyForecast?.DailyForecasts?.get(i)?.EpochDate?.toLong() ?: 0,
                        1,
                        zoneOffset
                    ).dayOfWeek
                )
            }

            val result by remember {
                mutableIntStateOf(
                    dailyForecast?.DailyForecasts?.get(i)?.Day?.Icon
                        ?: 0
                )
            }

            val iconResource = when (result) {
                1, 33 -> R.drawable.sunny
                2, 3, 34, 35 -> R.drawable.partlysunny
                6, 7, 8, 38 -> R.drawable.cloudy
                4, 36 -> R.drawable.intermittent_clouds
                11 -> R.drawable.fog
                12, 13, 14, 39, 40 -> R.drawable.showers
                15, 16, 17, 41, 42 -> R.drawable.thunderstorms
                18 -> R.drawable.rain
                19, 20, 21, 22, 23, 43, 44 -> R.drawable.snow
                24, 25, 26 -> R.drawable.ice
                29 -> R.drawable.rainandsnow
                30 -> R.drawable.hot
                31 -> R.drawable.cold
                32 -> R.drawable.windy
                else -> R.drawable.unknown

            }

            val shortPhrase by remember {
                mutableStateOf(dailyForecast?.DailyForecasts?.get(i)?.Day?.ShortPhrase ?: "")
            }
            val maximumTemp by remember {
                mutableStateOf("${dailyForecast?.DailyForecasts?.get(i)?.Temperature?.Maximum?.Value} °")
            }
            val minimumTemp by remember {
                mutableStateOf("${dailyForecast?.DailyForecasts?.get(i)?.Temperature?.Minimum?.Value}°")
            }
            val prep by remember {
                mutableStateOf("${dailyForecast?.DailyForecasts?.get(i)?.Day?.PrecipitationProbability}%")
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

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(start = 8.dp, bottom = 12.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.toString().substring(0, 3) + " -",
                        color = Color.Black,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = shortPhrase,
                        color = textColor,
                        fontFamily = fontFamily,
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 2.dp, end = 16.dp)
                            .horizontalScroll(state = rememberScrollState())
                    )
                }
                Row(
                    modifier = Modifier
                        .height(75.dp)
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconResource),
                        contentDescription = "icon",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .height(40.dp)
                            .padding(start = 16.dp, end = 24.dp)
                    )
                    Text(
                        text = maximumTemp,
                        color = Color(0xFFeb9534),
                        fontFamily = fontFamily,
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = minimumTemp,
                        color = Color(0xFF3483eb),
                        fontFamily = fontFamily,
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Image(
                        painter = painterResource(
                            id = R.drawable.ic_drop
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(),
                    )
                    Text(
                        text = prep,
                        color = textColor,
                        fontFamily = fontFamily,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.W300,
                        modifier = Modifier.padding(end = 8.dp, start = 8.dp)
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
