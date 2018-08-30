package com.testproject.weather.api;

import com.testproject.weather.entity.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API for getting weather info.
 */
public interface OpenWeatherMapApi {
    @GET("data/2.5/weather?units=metric")
    Call<Weather> getCurrentWeather(@Query("lat") String latitude,
                                    @Query("lon") String longitude,
                                    @Query("appid") String appid);
}
