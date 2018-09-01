package com.testproject.weather.places;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.testproject.weather.R;
import com.testproject.weather.map.MapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.testproject.weather.places.PlacesContract.ADD_PLACE_REQUEST;

public class PlacesActivity extends AppCompatActivity
        implements PlacesContract.View, LocationListener {

    public static final String LOG_TAG = PlacesActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 200;
    private static final int PLACES_LOADER_ID = 0;

    private PlacesContract.Presenter mPlacesPresenter;
    private LatLng currentMarkerPosition;

    @BindView(R.id.list_places)
    public RecyclerView mPlacesRecyclerView;

    @BindView(R.id.btn_add_place)
    public Button addPlaceButton;

    private PlacesCursorAdapter mPlacesCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);

        PlacesModel placesModel = new PlacesModel(this);
        PlacesPresenter placesPresenter = new PlacesPresenter(this, placesModel);

        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPlacesRecyclerView.addItemDecoration(new DividerItemDecoration(mPlacesRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mPlacesCursorAdapter = new PlacesCursorAdapter(this, null);
        mPlacesRecyclerView.setAdapter(mPlacesCursorAdapter);

        getLoaderManager().initLoader(PLACES_LOADER_ID, null, placesPresenter);

        if (savedInstanceState != null) {
            currentMarkerPosition = savedInstanceState.getParcelable("currentMarkerPosition");
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            } catch (SecurityException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    @OnClick(R.id.btn_add_place)
    @Override
    public void openMapView() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlacesActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
        startActivityForResult(mapIntent, ADD_PLACE_REQUEST);
    }

    @Override
    public void setCurrentMarkerPosition(LatLng currentMarkerPosition) {
        this.currentMarkerPosition = currentMarkerPosition;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPlacesPresenter.processResult(requestCode, resultCode, data);
    }

    @Override
    public void updatePlacesList(Cursor cursor) {
        mPlacesCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPlacesPresenter.start();
    }

    @Override
    public void setPresenter(PlacesContract.Presenter presenter) {
        mPlacesPresenter = presenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentMarkerPosition != null) {
            outState.putParcelable("currentMarkerPosition", currentMarkerPosition);
        }
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
