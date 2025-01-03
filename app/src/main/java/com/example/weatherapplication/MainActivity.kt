package com.example.weatherapplication

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapplication.data.WeatherRepoImple
import com.example.weatherapplication.presentation.FavoritesScreen
import com.example.weatherapplication.presentation.LoginScreen
import com.example.weatherapplication.presentation.MainViewModel
import com.example.weatherapplication.presentation.SearchScreen
import com.example.weatherapplication.presentation.WeatherApp
import com.example.weatherapplication.ui.theme.WeatherApplicationTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener

const val TAG = "MainActivity"

val fontFamily = FontFamily(
    Font(R.font.lexend_black, FontWeight.Black),
    Font(R.font.lexend_bold, FontWeight.Bold),
    Font(R.font.lexend_extrabold, FontWeight.ExtraBold),
    Font(R.font.lexend_extralight, FontWeight.ExtraLight),
    Font(R.font.lexend_light, FontWeight.Light),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_semibold, FontWeight.SemiBold),
    Font(R.font.lexend_thin, FontWeight.Thin)
)

var latitude: String? = ""
var longitude: String? = ""

class MainActivity : ComponentActivity() {

    private lateinit var permission: LocationPermission
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permission = LocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (permission.isLocationGranted(this)) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false

                }).addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Toast.makeText(this, "Cannot Get Location", Toast.LENGTH_SHORT).show()
                } else {
                    latitude = location.latitude.toString()
                    longitude = location.longitude.toString()

                }
            }
        } else {
            permission.requestLocationPermission(this)
        }

        val viewModel by viewModels<MainViewModel>(factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(WeatherRepoImple(RetrofitInstance.Api))
                            as T
                }
            }
        }
        )

        setContent {
            WeatherApplicationTheme {
                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf(false) }
                var username by remember { mutableStateOf("") }

                NavHost(
                    navController = navController,
                    startDestination = Screen.MainScreen.route
                ) {
                    composable(route = Screen.MainScreen.route) {
                        WeatherApp(navController = navController, mainViewModel = viewModel)
                    }

                    composable(
                        route = Screen.FavoritesScreen.route,
                    ) {
                        FavoritesScreen(mainViewModel = viewModel, onClose = { }, navController = navController)
                    }
                    composable(
                        route = Screen.SearchScreen.route
                    ) {
                        SearchScreen(mainViewModel = viewModel, navController = navController)
                    }
                    composable(
                        route = Screen.LoginScreen.route
                    ) {
                        LoginScreen(
                            navController = navController,
                            onLoginClicked = { enteredUserName ->
                                username = enteredUserName
                                isLoggedIn = true
                            },
                            isLoggedIn = isLoggedIn,
                            userName = username
                        )

                    }
                }
            }
        }
    }
}

fun getLatitudeandLongitude(): List<String> {
    return listOf("$latitude", "$longitude")
}