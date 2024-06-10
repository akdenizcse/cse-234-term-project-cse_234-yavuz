package com.example.weatherapplication.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.Result
import com.example.weatherapplication.data.WeatherRepository
import com.example.weatherapplication.getLatitudeandLongitude
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _showToastErrorChannel = Channel<Boolean>()
    val showToastErrorChannel = _showToastErrorChannel.receiveAsFlow()

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: MutableLiveData<String?> get() = _toastMessage
    private fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    init {
        isLoading(true)
        viewModelScope.launch {
            waitUntil()
            _uiState.update { currentState ->
                currentState.copy(
                    latitude = getLatitudeandLongitude()[0],
                    longitude = getLatitudeandLongitude()[1],
                )
            }
            getLocationData(
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude
            )
            delay(60)
            getCurrentData()
            getHourlyData()
            getDailyForecastData()

        }
    }

    private fun isLoading(isLoading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = isLoading
            )
        }
    }

    fun setLatitudeLongitude(latitude: String, longitude: String) {
        _uiState.update { currentState ->
            currentState.copy(
                latitude = latitude,
                longitude = longitude
            )
        }

    }

    private suspend fun waitUntil(interval: Long = 1000) {
        while (getLatitudeandLongitude() == listOf("", "")) {
            delay(interval)
        }
    }


    suspend fun getLocationData(latitude: String, longitude: String) {
        weatherRepository.getLocationData(
            apikey = _uiState.value.apiKey,
            q = "$latitude,$longitude"
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }
                }

                is Result.Success -> {
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

    suspend fun getLocationDataWithLocationKey(locationKey: String) {
        weatherRepository.getLocationDataWithLocationKey(
            apikey = _uiState.value.apiKey,
            locationKey = locationKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }
                }

                is Result.Success -> {
                    result.data?.let { locationData ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                locationData = locationData
                            )
                        }
                    }
                }

            }

        }
    }

    suspend fun getCurrentData() {
        weatherRepository.getCurrentData(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }

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

    suspend fun getHourlyData() {
        weatherRepository.getHourlyData(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }

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

    suspend fun getDailyForecastData() {
        weatherRepository.getDailyForecast(
            locationKey = _uiState.value.locationData?.Key ?: "",
            apikey = _uiState.value.apiKey
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }

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
            isLoading(false)
        }
    }


    private suspend fun getSearchResult(q: String) {
        weatherRepository.getKeyByTextSearch(
            apikey = _uiState.value.apiKey,
            q = q
        ).collectLatest { result ->
            when (result) {
                is Result.Error -> {
                    result.message?.let { showToast(it) }
                }

                is Result.Success -> {
                    result.data?.let { searchResult ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                searchResult = searchResult
                            )
                        }
                    }
                }
            }
        }
    }

    private val searchHandler: Handler = Handler(Looper.getMainLooper())
    private val SEARCH_DELAY: Long = 400
    private val MIN_SEARCH_LENGTH = 3
    private var searchRunnable: Runnable? = null
    fun searchProductsWithDelay(keyword: String) {
        // Check if the keyword length is at least MIN_SEARCH_LENGTH
        if (keyword.length < MIN_SEARCH_LENGTH) {
            _uiState.update { currentState ->
                currentState.copy(
                    searchResult = null
                )
            }
            return
        }

        // Remove any pending searchRunnable
        searchRunnable?.let { searchHandler.removeCallbacks(it) }

        // Create a new searchRunnable with a delay
        val localSearchRunnable = Runnable {
            // Perform the search after the delay
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    getSearchResult(keyword)
                }
            }
        }

        // Post the searchRunnable with a delay
        searchHandler.postDelayed(localSearchRunnable, SEARCH_DELAY)

        // Assign the localSearchRunnable to the mutable property
        searchRunnable = localSearchRunnable
    }


}
