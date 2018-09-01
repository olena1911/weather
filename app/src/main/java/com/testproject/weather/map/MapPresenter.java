package com.testproject.weather.map;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class MapPresenter implements MapContract.Presenter,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String LOG_TAG = MapPresenter.class.getSimpleName();

    private MapContract.View mMapView;
    private Geocoder mGeocoder;
    private GoogleApiClient mClient;

    public MapPresenter(MapContract.View mapView, Geocoder geocoder) {
        mMapView = mapView;
        mGeocoder = geocoder;
        mMapView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public String getPlaceName(LatLng position) throws IOException {
        List<Address> addresses = mGeocoder.getFromLocation(position.latitude, position.longitude, 1);
        String cityName = addresses.get(0).getLocality();
        String countryName = addresses.get(0).getCountryName();
        return (cityName != null) ? (cityName + ", " + countryName) : "Unknown city";
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
}
