package com.testproject.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;
import static com.testproject.weather.db.WeatherContract.PlaceEntry;

/**
 * Class-helper to create database.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = WeatherDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WEATHER_TABLE =  "CREATE TABLE " + WeatherEntry.TABLE_NAME + " ("
                + WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WeatherEntry.COLUMN_TIME + " INTEGER, "
                + WeatherEntry.COLUMN_PLACE_ID + " INTEGER NOT NULL, "
                + WeatherEntry.COLUMN_TEMPERATURE + " REAL NOT NULL, "
                + WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "
                + WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "
                + WeatherEntry.COLUMN_WEATHER_ICON_ID + " TEXT NOT NULL, "
                + WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL)";

        String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " ("
                + PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlaceEntry.COLUMN_PLACE_NAME + " TEXT NOT NULL, "
                + PlaceEntry.COLUMN_LATITUDE + " REAL NOT NULL, "
                + PlaceEntry.COLUMN_LONGITUDE + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
        Log.i(LOG_TAG, "Weather table is created");
        db.execSQL(SQL_CREATE_PLACES_TABLE);
        Log.i(LOG_TAG, "Places table is created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

