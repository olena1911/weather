package com.testproject.weather.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.testproject.weather.db.WeatherContract.WeatherEntry;
import static com.testproject.weather.db.WeatherContract.PlaceEntry;

/**
 * Class-provider to work with weather.db.
 */
public class WeatherProvider extends ContentProvider {

    public static final String LOG_TAG = WeatherProvider.class.getSimpleName();

    private static final int WEATHER = 100;
    private static final int PLACES = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, WEATHER);
        sUriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_PLACES, PLACES);
   }

    private WeatherDBHelper mWeatherDbHelper;

    @Override
    public boolean onCreate() {
        mWeatherDbHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mWeatherDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                cursor = database.query(WeatherEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PLACES:
                cursor = database.query(PlaceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                Log.e(LOG_TAG, "Cannot query unknown URI " + uri);
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return insertWeather(uri, contentValues);
            case PLACES:
                return insertPlace(uri, contentValues);
            default:
                Log.e(LOG_TAG, "Insertion is not supported for " + uri);
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertWeather(Uri uri, ContentValues values) {
        SQLiteDatabase database = mWeatherDbHelper.getWritableDatabase();

        long id = database.insert(WeatherEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertPlace(Uri uri, ContentValues values) {
        SQLiteDatabase database = mWeatherDbHelper.getWritableDatabase();

        long id = database.insert(PlaceEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return updateWeather(uri, contentValues, selection, selectionArgs);
            case PLACES:
                return updatePlace(uri, contentValues, selection, selectionArgs);
            default:
                Log.e(LOG_TAG, "Update is not supported for " + uri);
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateWeather(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mWeatherDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int updatePlace(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mWeatherDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(PlaceEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mWeatherDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                rowsDeleted = database.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACES:
                rowsDeleted = database.delete(PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                Log.e(LOG_TAG, "Deletion is not supported for " + uri);
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                return WeatherEntry.CONTENT_LIST_TYPE;
            case PLACES:
                return PlaceEntry.CONTENT_LIST_TYPE;
            default:
                Log.e(LOG_TAG, "Unknown URI " + uri + " with match " + match);
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}