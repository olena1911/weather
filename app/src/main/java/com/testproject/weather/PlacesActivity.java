package com.testproject.weather;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

import static com.testproject.weather.db.WeatherContract.PlaceEntry;

public class PlacesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String LOG_TAG = PlacesActivity.class.getSimpleName();

    private RecyclerView mPlacesRecyclerView;
    private PlacesCursorAdapter mPlacesCursorAdapter;

    private GoogleApiClient mClient;

    private static final int PLACES_LOADER_ID = 0;
    private static final int PLACE_PICKER_REQUEST = 100;
    public static final int TYPE_LOCALITY = 1009;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mPlacesRecyclerView = (RecyclerView) findViewById(R.id.list_places);
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlacesRecyclerView.addItemDecoration(new DividerItemDecoration(mPlacesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

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

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    public void addPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Need location permission", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(LOG_TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(LOG_TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(LOG_TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
           if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
               Place place = PlacePicker.getPlace(this, data);
                if (place == null) {
                    Log.i(LOG_TAG, "No place selected");
                    return;
                }

               String cityName = place.getName().toString();
               List<Integer> placeTypes = place.getPlaceTypes();
               if (!placeTypes.contains(TYPE_LOCALITY)) {
                   Log.d(LOG_TAG, "Only cities should be selected");
                   return;
               }

               ContentValues values = new ContentValues();
               values.put(PlaceEntry.COLUMN_CITY_NAME, cityName);
               Uri newUri = getContentResolver().insert(PlaceEntry.CONTENT_URI, values);
            }
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

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        // TODO refreshPlacesData();
        Log.i(LOG_TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG_TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(LOG_TAG, "API Client Connection Failed!");
    }


}
