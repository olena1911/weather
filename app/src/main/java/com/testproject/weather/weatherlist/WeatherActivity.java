package com.testproject.weather.weatherlist;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.testproject.weather.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {

    public static final String LOG_TAG = WeatherActivity.class.getSimpleName();

    private static final int WEATHER_LOADER_ID = 1;

    private WeatherContract.Presenter mPresenter;

    @BindView(R.id.list_weather)
    public RecyclerView mWeatherRecyclerView;

    private WeatherCursorAdapter mWeatherCursorAdapter;

    @BindView(R.id.btn_check_weather)
    public Button checkWeatherButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent weatherIntent = getIntent();
        String placeName = weatherIntent.getStringExtra("placeName");
        int placeId = weatherIntent.getIntExtra("placeId", -1);
        double latitude = weatherIntent.getDoubleExtra("latitude", -1);
        double longitude = weatherIntent.getDoubleExtra("longitude", -1);
        setContentView(R.layout.activity_weather_list);
        getSupportActionBar().setTitle(placeName);
        ButterKnife.bind(this);

        WeatherModel weatherModel = new WeatherModel(this);
        WeatherPresenter weatherPresenter = new WeatherPresenter(this, weatherModel, placeId, latitude, longitude);

        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWeatherRecyclerView.addItemDecoration(new DividerItemDecoration(mWeatherRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mWeatherCursorAdapter = new WeatherCursorAdapter(this, null);
        mWeatherRecyclerView.setAdapter(mWeatherCursorAdapter);

        getLoaderManager().initLoader(WEATHER_LOADER_ID, null, weatherPresenter);
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showWeatherList(Cursor cursor) {
        mWeatherCursorAdapter.swapCursor(cursor);
    }

    @OnClick(R.id.btn_check_weather)
    public void checkWeather() {
        mPresenter.checkWeather();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }
}
