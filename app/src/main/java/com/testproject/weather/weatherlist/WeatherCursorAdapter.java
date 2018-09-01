package com.testproject.weather.weatherlist;

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
import com.testproject.weather.R;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        int timeColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_TIME);
        int weatherIconIdIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ICON_ID);
        int temperatureColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_TEMPERATURE);
        int humidityColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY);
        int pressureColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_PRESSURE);
        int windSpeedColumnIndex = mCursor.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED);

        long time = mCursor.getLong(timeColumnIndex);
        Date date = new Date(time);
        String dateString =  new SimpleDateFormat("MMM dd").format(date);
        String timeString =  new SimpleDateFormat("HH:mm").format(date);

        String weatherIconId = mCursor.getString(weatherIconIdIndex);
        String temperature = String.valueOf(mCursor.getDouble(temperatureColumnIndex)) + " \u2103";
        String humidity = String.valueOf(mCursor.getDouble(humidityColumnIndex)) + " %";
        String pressure = String.valueOf(mCursor.getDouble(pressureColumnIndex)) + " hPa";
        String windSpeed = String.valueOf(mCursor.getDouble(windSpeedColumnIndex)) + " m/s";

        weatherViewHolder.bindFields(dateString, timeString, weatherIconId, temperature, humidity, pressure, windSpeed);
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
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView temperatureTextView;
        private TextView pressureTextView;
        private TextView humidityTextView;
        private ImageView weatherIconImageView;
        private TextView windSpeedTextView;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.text_date);
            timeTextView = itemView.findViewById(R.id.text_time);
            temperatureTextView = itemView.findViewById(R.id.text_temperature);
            pressureTextView = itemView.findViewById(R.id.text_pressure);
            humidityTextView = itemView.findViewById(R.id.text_humidity);
            weatherIconImageView = itemView.findViewById(R.id.image_weather_icon);
            windSpeedTextView = itemView.findViewById(R.id.text_wind_speed);
        }

        public void bindFields(String dateString, String timeString, String weatherIconId,
                               String temperature, String humidity, String pressure,
                               String windSpeed) {
            dateTextView.setText(dateString);
            timeTextView.setText(timeString);
            Picasso.get().load(
                    itemView.getContext().getString(R.string.owp_weather_icon_path)
                            + weatherIconId
                            + itemView.getContext().getString(R.string.owp_weather_icon_extension))
                    .fit().centerInside().into(weatherIconImageView);
            temperatureTextView.setText(temperature);
            pressureTextView.setText(pressure);
            humidityTextView.setText(humidity);
            windSpeedTextView.setText(windSpeed);
        }
    }
}