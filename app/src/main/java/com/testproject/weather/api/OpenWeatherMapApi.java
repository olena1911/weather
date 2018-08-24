package com.testproject.weather.api;

import com.testproject.weather.entity.Weather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface OpenWeatherMapApi {
    @GET("data/2.5/weather?q=London&appid=5ca364952b79a32581152f088c8d10cb")
    Call<Weather> getCurrentWeather();
}
