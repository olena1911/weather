package com.testproject.weather;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class WeatherListActivity extends Activity {

    private RecyclerView mWeatherRecyclerView;
    private WeatherCursorAdapter mWeatherCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);

        mWeatherRecyclerView = (RecyclerView) findViewById(R.id.list_weather);
        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWeatherCursorAdapter = new WeatherCursorAdapter(this, null);
        mWeatherRecyclerView.setAdapter(mWeatherCursorAdapter);
    }
}
