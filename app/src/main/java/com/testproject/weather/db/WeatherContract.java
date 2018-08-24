package com.testproject.weather.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {

    public static final String CONTENT_AUTHORITY = "com.testproject.weather";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WEATHER = "weather";
    public static final String PATH_PLACES = "places";

    private WeatherContract() {
    }

    public static class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WEATHER);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_MIN_TEMPERATURE = "min_temperature";
        public static final String COLUMN_MAX_TEMPERATURE = "max_temperature";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DEGREE = "wind_degree";
        public static final String COLUMN_CLOUDINESS = "cloudiness";
    }

    public static class PlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLACES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;

        public static final String TABLE_NAME = "places";

        public final static String _ID = BaseColumns._ID;

        public static final String COLUMN_CITY_NAME = "city_name";
    }
}
