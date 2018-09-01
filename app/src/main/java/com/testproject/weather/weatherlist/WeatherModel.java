package com.testproject.weather.weatherlist;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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

public class WeatherModel implements WeatherContract.Model {

    public static final String LOG_TAG = WeatherModel.class.getSimpleName();

    private Context mContext;

    public WeatherModel(Context context) {
        mContext = context;
    }

    @Override
    public Loader<Cursor> getWeatherList(int placeId) {
        String[] projection = {
                com.testproject.weather.db.WeatherContract.WeatherEntry._ID,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_TIME,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_PLACE_ID,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_TEMPERATURE,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_WEATHER_ICON_ID,
                com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_WIND_SPEED
        };

        String selection = com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_PLACE_ID + "=?";

        String[] selectionArgs = {String.valueOf(placeId)};

        // Show new weather first
        String sortOrder = com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_TIME + " DESC";

        return new CursorLoader(mContext,
                com.testproject.weather.db.WeatherContract.WeatherEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    public void checkWeather(final int placeId, double latitude, double longitude) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        WeatherDeserializer deserializer = new WeatherDeserializer();
        gsonBuilder.registerTypeAdapter(Weather.class, deserializer);
        Gson customGson = gsonBuilder.create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.owp_baseurl))
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .build();

        OpenWeatherMapApi weatherApi = retrofit.create(OpenWeatherMapApi.class);

        Call<Weather> currentWeatherCall
                = weatherApi.getCurrentWeather(
                String.valueOf(latitude),
                String.valueOf(longitude),
                mContext.getString(R.string.appid));

        currentWeatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    Weather currentWeather = response.body();

                    ContentValues values = new ContentValues();
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_TIME, currentWeather.getTime());
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_PLACE_ID, placeId);
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_TEMPERATURE, currentWeather.getTemperature());
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_HUMIDITY, currentWeather.getHumidity());
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_PRESSURE, currentWeather.getPressure());
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_WEATHER_ICON_ID, currentWeather.getWeatherIconId());
                    values.put(com.testproject.weather.db.WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, currentWeather.getWindSpeed());
                    Uri newUri = mContext.getContentResolver().insert(com.testproject.weather.db.WeatherContract.WeatherEntry.CONTENT_URI, values);
                } else {
                    Log.e(LOG_TAG, "Response is not successful.");
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Log.e(LOG_TAG, "Fail to connect to weather API: " + t.getMessage());
            }
        });
    }
}
