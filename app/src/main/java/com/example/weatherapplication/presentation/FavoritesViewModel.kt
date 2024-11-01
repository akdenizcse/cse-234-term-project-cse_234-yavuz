package com.example.weatherapplication.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.MainApplication
import com.example.weatherapplication.data.db.FavoriteCity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
) : ViewModel() {

    private val cityDao = MainApplication.favoriteCitiesDatabase.getCityDao()

    private val _favoriteCities = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCity>> = _favoriteCities

    private val _uiState = MutableStateFlow(FavScreenUiState())
    val uiState: StateFlow<FavScreenUiState> = _uiState.asStateFlow()

    private val _showToastErrorChannel = Channel<Boolean>()
    val showToastErrorChannel = _showToastErrorChannel.receiveAsFlow()

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: MutableLiveData<String?> get() = _toastMessage

    init {
        viewModelScope.launch {
            cityDao.getAllFavCities().collect { cities ->
                _favoriteCities.value = cities
            }
        }
    }

    fun addFavCity(cityName: String, latitude:String, longitude: String) {
        if (favoriteCities.value.size < 10) {
            viewModelScope.launch {
                cityDao.insertCity(FavoriteCity(cityName, latitude, longitude))
            }
            showToast("Added Successfully")
        } else {
            viewModelScope.launch {
                showToast("Cannot add more than 10 cities")
            }

        }
    }

    fun deleteFavCity(cityName: String) {
        viewModelScope.launch {
            cityDao.deleteCity(cityName)
            showToast("Deleted Successfully")
        }
    }


    private fun showToast(message: String) {
        _toastMessage.value = message
    }
    fun clearToastMessage() {
        _toastMessage.value = null
    }
}