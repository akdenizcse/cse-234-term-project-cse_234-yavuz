package com.example.weatherapplication.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.TAG
import com.example.weatherapplication.data.Result
import com.example.weatherapplication.data.WeatherRepository
import com.example.weatherapplication.getLatandLong
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _showToastErrorChannel = Channel<Boolean>()

    val showToastErrorChannel = _showToastErrorChannel.receiveAsFlow()


    init {
        viewModelScope.launch {

            // wait for the initialization of latitude and longitude
            waitUntil()

            _uiState.update { currentState ->
                currentState.copy(
                    lat_lon = getLatandLong()
                )
            }
            delay(500)
            getLocationData()
            Log.d(TAG, "after location search" + _uiState.value.locationData.toString())
            delay(2000)
            getCurrentData()
            getHourlyData()
            getDailyForecastData()

        }
    }

    private suspend fun waitUntil(interval: Long = 1000) {
        while (getLatandLong() == ",") {
            delay(interval)
        }
    }

    private suspend fun getLocationData() {

        weatherRepository.getLocationData(
            apikey = _uiState.value.apiKey,
            q = _uiState.value.lat_lon
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    _showToastErrorChannel.send(true)
                    Log.d(TAG, "ERROR IN LOCATION API")
                }

                is Result.Success -> {
                    Log.d(TAG, "NOERROR IN LOCATION API")

                    result.data?.let { LocationData ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                locationData = LocationData
                            )
                        }
                    }
                }
            }
        }

    }

    private suspend fun getCurrentData() {

        weatherRepository.getCurrentData(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    _showToastErrorChannel.send(true)
                }

                is Result.Success -> {
                    result.data?.let { CurrentConditions ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                currentData = CurrentConditions
                            )
                        }
                    }
                }
            }
        }


    }

    private suspend fun getHourlyData() {

        weatherRepository.getHourlyData(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    _showToastErrorChannel.send(true)
                }

                is Result.Success -> {
                    result.data?.let { hourlyData ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                hourlyData = hourlyData
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getDailyForecastData() {

        weatherRepository.getDailyForecast(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    _showToastErrorChannel.send(true)
                }

                is Result.Success -> {
                    result.data?.let { dailyForecast ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                dailyData = dailyForecast
                            )
                        }
                    }
                }
            }
        }
    }
}
