package com.testproject.weather;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Marker currentMarker;
    private LatLng currentMarkerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (savedInstanceState != null) {
            currentMarkerPosition = savedInstanceState.getParcelable("currentMarkerPosition");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentMarker != null) {
            outState.putParcelable("currentMarkerPosition", currentMarkerPosition);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (currentMarkerPosition != null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentMarkerPosition));
        }
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
            currentMarkerPosition = null;
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            currentMarkerPosition = currentMarker.getPosition();
        }
    }

}
