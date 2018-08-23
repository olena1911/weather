package com.testproject.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.testproject.weather.db.WeatherContract.Weather;

public class WeatherDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = WeatherDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO? move to the WeatherContract
        String SQL_CREATE_WEATHER_TABLE =  "CREATE TABLE " + Weather.TABLE_NAME + " ("
                + Weather._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Weather.COLUMN_CITY_NAME + " TEXT NOT NULL, "
                + Weather.COLUMN_TEMPERATURE + " REAL NOT NULL, "
                + Weather.COLUMN_PRESSURE + " REAL NOT NULL, "
                + Weather.COLUMN_HUMIDITY + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

