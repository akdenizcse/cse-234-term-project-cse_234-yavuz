package com.example.weatherapplication.presentation

import com.example.weatherapplication.data.db.FavoriteCity

data class FavScreenUiState(
    val favLocations : List<FavoriteCity>? = null,
    val isSheetOpen: Boolean?=false
)