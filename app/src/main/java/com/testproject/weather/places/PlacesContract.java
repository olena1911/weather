package com.testproject.weather.places;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.testproject.weather.BasePresenter;
import com.testproject.weather.BaseView;

public class PlacesContract {

    public static int ADD_PLACE_REQUEST = 100;

    interface View extends BaseView<Presenter> {
        void openMapView();
        void updatePlacesList(Cursor cursor);
        void setCurrentMarkerPosition(LatLng position);
    }

    interface Presenter extends BasePresenter {
        void addPlace(String placeName, LatLng position);
        void processResult(int requestCode, int resultCode, Intent data);
    }

    interface Model {
        void insert(String placeName, LatLng position);
        Loader<Cursor> getPlaces();
    }
}
