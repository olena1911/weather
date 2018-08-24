package com.testproject.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PlacesActivity extends AppCompatActivity {

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

        //getLoaderManager().initLoader(PLACES_LOADER_ID, null, this);
    }
}
