package com.testproject.weather;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.testproject.weather.api.OpenWeatherMapApi;
import com.testproject.weather.entity.Weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;

public class WeatherListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mWeatherRecyclerView;
    private WeatherCursorAdapter mWeatherCursorAdapter;
    private String cityName;

    private static final int WEATHER_LOADER_ID = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cityName = getIntent().getStringExtra("cityName");
        setContentView(R.layout.activity_weather_list);

        try {
        getSupportActionBar().setTitle(cityName);}
        catch(Exception e) {
            Log.d("weatheractivity", e.getMessage());
        }

        Button checkWeatherButton = findViewById(R.id.btn_check_weather);
        checkWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkWeather();
            }
        });

        mWeatherRecyclerView = (RecyclerView) findViewById(R.id.list_weather);
        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWeatherCursorAdapter = new WeatherCursorAdapter(this, null);
        mWeatherRecyclerView.setAdapter(mWeatherCursorAdapter);

        getLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);
    }

    private void checkWeather() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        WeatherDeserializer deserializer = new WeatherDeserializer();
        gsonBuilder.registerTypeAdapter(Weather.class, deserializer);

        Gson customGson = gsonBuilder.create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create(customGson))
                .build();

        OpenWeatherMapApi weatherApi = retrofit.create(OpenWeatherMapApi.class);

        Call<Weather> currentWeatherCall = weatherApi.getCurrentWeather(cityName);

        currentWeatherCall.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    Weather currentWeather = response.body();

                    ContentValues values = new ContentValues();
                    values.put(WeatherEntry.COLUMN_CITY_NAME, currentWeather.getCityName());
                    values.put(WeatherEntry.COLUMN_TEMPERATURE, currentWeather.getTemperature());
                    values.put(WeatherEntry.COLUMN_HUMIDITY, currentWeather.getHumidity());
                    values.put(WeatherEntry.COLUMN_PRESSURE, currentWeather.getPressure());
                    Uri newUri = getContentResolver().insert(WeatherEntry.CONTENT_URI, values);
                    Log.d("weatherretrofit", "newuri " + newUri);
                } else {
                    // TODO
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                // TODO
            }
        });
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                WeatherEntry._ID,
                WeatherEntry.COLUMN_CITY_NAME,
                WeatherEntry.COLUMN_TEMPERATURE,
                WeatherEntry.COLUMN_PRESSURE,
                WeatherEntry.COLUMN_HUMIDITY};

        String selection = WeatherEntry.COLUMN_CITY_NAME + "=?";

        String[] selectionArgs = {cityName};

        return new CursorLoader(this,
                WeatherEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mWeatherCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mWeatherCursorAdapter.swapCursor(null);
    }
}
