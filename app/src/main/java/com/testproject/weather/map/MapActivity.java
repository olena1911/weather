package com.testproject.weather.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.testproject.weather.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        MapContract.View, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String LOG_TAG = MapActivity.class.getSimpleName();

    private MapContract.Presenter mMapPresenter;
    private GoogleApiClient mGoogleApiClient;

    private GoogleMap mMap;
    private Marker currentMarker;
    private LatLng currentMarkerPosition;
    private String cityName;
    private String countryName;

    @BindView(R.id.btn_ok)
    public Button okButton;

    @BindView(R.id.btn_cancel)
    public Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        MapPresenter mapPresenter = new MapPresenter(this, new Geocoder(this, Locale.ENGLISH));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            // get position previously selected on map
            currentMarkerPosition = savedInstanceState.getParcelable("currentMarkerPosition");
        } else {
            // get last saved position
            currentMarkerPosition = getIntent().getParcelableExtra("currentMarkerPosition");
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
            addMarker(currentMarkerPosition);
        }
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void addMarker(LatLng position) {
        currentMarker = mMap.addMarker(new MarkerOptions().position(position));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
        currentMarkerPosition = currentMarker.getPosition();
        String placeName;
        try {
            placeName = mMapPresenter.getPlaceName(currentMarkerPosition);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            placeName = getString(R.string.title_unknown_city);
        }
        currentMarker.setTitle(placeName);
        currentMarker.showInfoWindow();
        okButton.setEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
            currentMarker = null;
            currentMarkerPosition = null;
            cityName = null;
            countryName = null;
            okButton.setEnabled(false);
        } else {
            addMarker(latLng);
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    @OnClick(R.id.btn_ok)
    @Override
    public void openChoosePlaceNameDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MapActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_place, null);
        dialogBuilder.setView(dialogView);

        final EditText placeNameEditText = dialogView.findViewById(R.id.edit_place_name);
        if (cityName != null) {
            placeNameEditText.setText(cityName + ", " + countryName);
        } else {
            double latitude = new BigDecimal(currentMarkerPosition.latitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            double longitude = new BigDecimal(currentMarkerPosition.longitude).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            placeNameEditText.setText("New place [" + latitude + ", " + longitude + "]");
        }

        dialogBuilder.setPositiveButton(R.string.btn_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("placeName", placeNameEditText.getText().toString());
                resultIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        AlertDialog choosePlaceNameDialog = dialogBuilder.create();
        choosePlaceNameDialog.show();
    }

    @OnClick(R.id.btn_cancel)
    public void cancelAndCloseMap() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapPresenter.start();
    }

    @Override
    public void setPresenter(MapContract.Presenter presenter) {
        mMapPresenter = presenter;
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
