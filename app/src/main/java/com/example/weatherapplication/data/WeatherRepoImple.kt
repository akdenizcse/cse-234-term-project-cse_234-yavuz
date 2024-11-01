package com.example.weatherapplication.data

import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import com.example.weatherapplication.data.model.HourlyData.HourlyData
import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.data.model.SearchValues.SearchValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException

class WeatherRepoImple(
    private val api:WeatherApi

): WeatherRepository {
    override suspend fun getLocationData(apikey: String, q:String): Flow<Result<LocationData>> {
        return flow {
            val locationFromApi = try {
                api.getLocationData(apikey = apikey, q = q)
                //api.getLocationData()
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow

            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(locationFromApi.body()))
        }
    }

    override suspend fun getLocationDataWithLocationKey(
        apikey: String,
        locationKey: String
    ): Flow<Result<LocationData>> {
        return flow {
            val locationDataFromApi = try{
                api.getLocationDataWithLocationKey(apikey = apikey, locationKey = locationKey)
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow

            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(locationDataFromApi.body()))
        }
    }

    override suspend fun getCurrentData(locationKey: String, apikey: String): Flow<Result<CurrentConditions>> {
        return flow {
            val currentConditionsFromApi = try {
                api.getCurrentConditions(apikey = apikey, locationKey = locationKey)
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow

            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(currentConditionsFromApi.body()))
        }
    }

    override suspend fun getHourlyData(
        locationKey: String,
        apikey: String
    ): Flow<Result<List<HourlyData>>> {
        return  flow {
            val hourlyData = try{
                api.getHourlyData(locationKey, apikey)
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(hourlyData.body()))
        }
    }

    override suspend fun getDailyForecast(
        locationKey: String,
        apikey: String
    ): Flow<Result<DailyWeatherForecast>> {
        return  flow {
            val dailyForecastData = try{
                api.getDailyForecast(locationKey, apikey)
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(dailyForecastData.body()))
        }
    }

    override suspend fun getKeyByTextSearch(
        apikey: String,
        q: String
    ): Flow<Result<List<SearchValues>>> {
        return  flow {
            val searchResults = try{
                api.getKeyByTextSearch(apikey, q)
            }catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "IO Exception"))
                return@flow
            }catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "HTTP Exception"))
                return@flow
            }
            emit(Result.Success(searchResults.body()))
        }
    }


}