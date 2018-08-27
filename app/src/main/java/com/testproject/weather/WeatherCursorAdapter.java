package com.testproject.weather;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;

public class WeatherCursorAdapter extends RecyclerView.Adapter<WeatherCursorAdapter.WeatherViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public WeatherCursorAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_weather, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder weatherViewHolder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        int cityNameColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_CITY_NAME);
        int temperatureColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_TEMPERATURE);
        int pressureColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
        int humidityColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
        int weatherIconIdIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ICON_ID);

        String cityName = mCursor.getString(cityNameColumnIndex);
        String temperature = String.valueOf(mCursor.getDouble(temperatureColumnIndex));
        String pressure = String.valueOf(mCursor.getDouble(pressureColumnIndex));
        String humidity = String.valueOf(mCursor.getDouble(humidityColumnIndex));
        String weatherIconId = mCursor.getString(weatherIconIdIndex);

        weatherViewHolder.fillFields(cityName, temperature, pressure, humidity, weatherIconId);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
    public class WeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView cityNameTextView;
        private TextView temperatureTextView;
        private TextView pressureTextView;
        private TextView humidityTextView;
        private ImageView weatherIconImageView;

        public WeatherViewHolder(View itemView) {
            super(itemView);

            cityNameTextView = (TextView) itemView.findViewById(R.id.city_name);
            temperatureTextView = (TextView) itemView.findViewById(R.id.temperature);
            pressureTextView = (TextView) itemView.findViewById(R.id.pressure);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidity);
            weatherIconImageView = (ImageView) itemView.findViewById(R.id.weather_icon);
        }

        public void fillFields(String cityName, String temperature, String pressure, String humidity, String weatherIconId) {
            cityNameTextView.setText(cityName);
            temperatureTextView.setText(temperature);
            pressureTextView.setText(pressure);
            humidityTextView.setText(humidity);
            Picasso.get().load("http://openweathermap.org/img/w/" + weatherIconId + ".png").into(weatherIconImageView);
        }
    }
}