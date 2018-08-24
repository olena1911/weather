package com.testproject.weather;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.testproject.weather.db.WeatherContract;

import static com.testproject.weather.db.WeatherContract.PlaceEntry;

public class PlacesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mPlacesRecyclerView;
    private PlacesCursorAdapter mPlacesCursorAdapter;

    private static final int PLACES_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mPlacesRecyclerView = (RecyclerView) findViewById(R.id.list_places);
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPlacesCursorAdapter = new PlacesCursorAdapter(this, null);
        mPlacesRecyclerView.setAdapter(mPlacesCursorAdapter);

        Button addPlaceButton = findViewById(R.id.btn_add_place);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlace();
            }
        });

        getLoaderManager().initLoader(PLACES_LOADER_ID, null, this);
    }

    public void addPlace() {
        ContentValues values = new ContentValues();
        values.put(PlaceEntry.COLUMN_CITY_NAME, "London");
        Uri newUri = getContentResolver().insert(PlaceEntry.CONTENT_URI, values);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                PlaceEntry._ID,
                PlaceEntry.COLUMN_CITY_NAME};

        return new CursorLoader(this,
                PlaceEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mPlacesCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mPlacesCursorAdapter.swapCursor(null);
    }
}
