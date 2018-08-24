package com.testproject.weather;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;

public class WeatherListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mWeatherRecyclerView;
    private WeatherCursorAdapter mWeatherCursorAdapter;

    private static final int WEATHER_LOADER_ID = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_list);

        mWeatherRecyclerView = (RecyclerView) findViewById(R.id.list_weather);
        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mWeatherCursorAdapter = new WeatherCursorAdapter(this, null);
        mWeatherRecyclerView.setAdapter(mWeatherCursorAdapter);

        getLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);
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

        return new CursorLoader(this,
                WeatherEntry.CONTENT_URI,
                projection,
                null,
                null,
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
