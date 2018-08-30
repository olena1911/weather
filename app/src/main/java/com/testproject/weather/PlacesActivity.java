package com.testproject.weather;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.testproject.weather.db.WeatherContract;

import java.math.BigDecimal;

import static com.testproject.weather.db.WeatherContract.PlaceEntry;

public class PlacesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String LOG_TAG = PlacesActivity.class.getSimpleName();

    private RecyclerView mPlacesRecyclerView;
    private PlacesCursorAdapter mPlacesCursorAdapter;
    private LatLng currentMarkerPosition;

    private GoogleApiClient mClient;

    private static final int PLACES_LOADER_ID = 0;
    private static final int ADD_PLACE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mPlacesRecyclerView = findViewById(R.id.list_places);
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
        if (savedInstanceState != null) {
            currentMarkerPosition = savedInstanceState.getParcelable("currentMarkerPosition");
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } catch (SecurityException e) {
                // TODO remove it
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentMarkerPosition != null) {
            outState.putParcelable("currentMarkerPosition", currentMarkerPosition);
        }
    }

    public void addPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Need location permission", Toast.LENGTH_LONG).show();
            return;
        }
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
        startActivityForResult(mapIntent, ADD_PLACE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_PLACE_REQUEST) {
            String placeName = data.getStringExtra("placeName");
            currentMarkerPosition = data.getParcelableExtra("currentMarkerPosition");
            if (resultCode == RESULT_OK) {
                ContentValues values = new ContentValues();
                values.put(WeatherContract.PlaceEntry.COLUMN_PLACE_NAME, placeName);
                double latitude = new BigDecimal(currentMarkerPosition.latitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                double longitude = new BigDecimal(currentMarkerPosition.longitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                values.put(WeatherContract.PlaceEntry.COLUMN_LATITUDE, latitude);
                values.put(WeatherContract.PlaceEntry.COLUMN_LONGITUDE, longitude);

                Uri newUri = getContentResolver().insert(WeatherContract.PlaceEntry.CONTENT_URI, values);
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                PlaceEntry._ID,
                PlaceEntry.COLUMN_PLACE_NAME,
                PlaceEntry.COLUMN_LATITUDE,
                PlaceEntry.COLUMN_LONGITUDE};

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

    @Override
    public void onLocationChanged(Location location) {
        currentMarkerPosition = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
