package com.testproject.weather.places;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.testproject.weather.db.WeatherContract;

import java.math.BigDecimal;

public class PlacesModel implements PlacesContract.Model {

    private Context mContext;

    public PlacesModel(Context context) {
        mContext = context;
    }

    @Override
    public void insert(String placeName, LatLng position) {
        ContentValues values = new ContentValues();
        values.put(WeatherContract.PlaceEntry.COLUMN_PLACE_NAME, placeName);
        double latitude = new BigDecimal(position.latitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        double longitude = new BigDecimal(position.longitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        values.put(WeatherContract.PlaceEntry.COLUMN_LATITUDE, latitude);
        values.put(WeatherContract.PlaceEntry.COLUMN_LONGITUDE, longitude);

        Uri newUri = mContext.getContentResolver().insert(WeatherContract.PlaceEntry.CONTENT_URI, values);

    }

    @Override
    public Loader<Cursor> getPlaces() {
        String[] projection = {
                WeatherContract.PlaceEntry._ID,
                WeatherContract.PlaceEntry.COLUMN_PLACE_NAME,
                WeatherContract.PlaceEntry.COLUMN_LATITUDE,
                WeatherContract.PlaceEntry.COLUMN_LONGITUDE};

        return new CursorLoader(mContext,
                WeatherContract.PlaceEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
}
