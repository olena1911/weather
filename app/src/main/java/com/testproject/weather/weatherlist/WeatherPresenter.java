package com.testproject.weather.weatherlist;

import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.LoaderManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.testproject.weather.R;
import com.testproject.weather.WeatherDeserializer;
import com.testproject.weather.api.OpenWeatherMapApi;
import com.testproject.weather.entity.Weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherPresenter implements WeatherContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private WeatherContract.View mWeatherView;
    private WeatherContract.Model mWeatherModel;
    private int mPlaceId;
    private double mLatitude;
    private double mLongitude;



    public WeatherPresenter(WeatherContract.View weatherView, WeatherContract.Model weatherModel, int placeId,
                            double latitude, double longitude) {
        mWeatherView = weatherView;
        mWeatherModel = weatherModel;
        mPlaceId = placeId;
        mLatitude = latitude;
        mLongitude = longitude;
        mWeatherView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void checkWeather() {
        mWeatherModel.checkWeather(mPlaceId, mLatitude, mLongitude);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return mWeatherModel.getWeatherList(mPlaceId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mWeatherView.showWeatherList(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mWeatherView.showWeatherList(null);
    }
}
