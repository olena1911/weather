package com.testproject.weather.weatherlist;

import android.content.Loader;
import android.database.Cursor;

import com.testproject.weather.BasePresenter;
import com.testproject.weather.BaseView;

public class WeatherContract     {
    interface View extends BaseView<WeatherContract.Presenter> {
        void showWeatherList(Cursor cursor);
    }

    interface Presenter extends BasePresenter {
        void checkWeather();
    }

    interface Model {
        Loader<Cursor> getWeatherList(int placeId);
        void checkWeather(int placeId, double latitude, double longitude);
    }
}