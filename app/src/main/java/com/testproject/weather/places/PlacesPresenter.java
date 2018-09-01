package com.testproject.weather.places;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import static android.app.Activity.RESULT_OK;

public class PlacesPresenter implements PlacesContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private PlacesContract.View mPlacesView;
    private PlacesContract.Model mPlacesModel;

    public PlacesPresenter(PlacesContract.View placesView, PlacesContract.Model placesModel) {
        mPlacesView = placesView;
        mPlacesModel = placesModel;
        mPlacesView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void processResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == PlacesContract.ADD_PLACE_REQUEST) && (resultCode == RESULT_OK)) {
            String placeName = data.getStringExtra("placeName");
            LatLng currentMarkerPosition = data.getParcelableExtra("currentMarkerPosition");
            addPlace(placeName, currentMarkerPosition);
            mPlacesView.setCurrentMarkerPosition(currentMarkerPosition);
        }
    }

    @Override
    public void addPlace(String placeName, LatLng position) {
        mPlacesModel.insert(placeName, position);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return mPlacesModel.getPlaces();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mPlacesView.updatePlacesList(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mPlacesView.updatePlacesList(null);
    }
}
