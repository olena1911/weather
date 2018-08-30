package com.testproject.weather;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.testproject.weather.db.WeatherContract;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    public static final String LOG_TAG = MapActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Marker currentMarker;
    private LatLng currentMarkerPosition;
    private String cityName;
    private String countryName;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

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

        okButton = findViewById(R.id.btn_ok);
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

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("currentMarkerPosition", currentMarkerPosition);
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        });

    }

    private void addMarker(LatLng latLng) {
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        currentMarkerPosition = currentMarker.getPosition();
        Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
        try {
            List<Address> addresses = gcd.getFromLocation(currentMarkerPosition.latitude, currentMarkerPosition.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                countryName = addresses.get(0).getCountryName();
            }

            if (cityName != null) {
                currentMarker.setTitle(cityName + ", " + countryName);
            } else {
                currentMarker.setTitle(getString(R.string.title_unknown_city));
            }
            currentMarker.showInfoWindow();
            okButton.setEnabled(true);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
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
}
