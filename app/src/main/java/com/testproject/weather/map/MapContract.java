package com.testproject.weather.map;

import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.testproject.weather.BasePresenter;
import com.testproject.weather.BaseView;

import java.io.IOException;

public class MapContract {
    interface View extends BaseView<MapContract.Presenter> {
        void openChoosePlaceNameDialog();
        void addMarker(LatLng position);
    }

    interface Presenter extends BasePresenter {
        String getPlaceName(LatLng position) throws IOException;
    }
}
