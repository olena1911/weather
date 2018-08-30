package com.testproject.weather;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.testproject.weather.db.WeatherContract.PlaceEntry;

public class PlacesCursorAdapter extends RecyclerView.Adapter<PlacesCursorAdapter.PlaceViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public PlacesCursorAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @NonNull
    @Override
    public PlacesCursorAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlacesCursorAdapter.PlaceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_place, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesCursorAdapter.PlaceViewHolder placeViewHolder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        int placeIdColumnIndex = mCursor.getColumnIndex(PlaceEntry._ID);
        int placeNameColumnIndex = mCursor.getColumnIndex(PlaceEntry.COLUMN_PLACE_NAME);
        int latitudeColumnIndex = mCursor.getColumnIndex(PlaceEntry.COLUMN_LATITUDE);
        int longitudeColumnIndex = mCursor.getColumnIndex(PlaceEntry.COLUMN_LONGITUDE);

        final int placeId = mCursor.getInt(placeIdColumnIndex);
        final String placeName = mCursor.getString(placeNameColumnIndex);
        final double latitude = mCursor.getDouble(latitudeColumnIndex);
        final double longitude = mCursor.getDouble(longitudeColumnIndex);

        placeViewHolder.fillFields(placeName, latitude, longitude);

        placeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent weatherIntent = new Intent(mContext, WeatherActivity.class);
                weatherIntent.putExtra("placeId", placeId);
                weatherIntent.putExtra("placeName", placeName);
                weatherIntent.putExtra("latitude", latitude);
                weatherIntent.putExtra("longitude", longitude);
                mContext.startActivity(weatherIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        private TextView placeNameTextView;
        private TextView placeCoordinatesTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.text_place_name);
            placeCoordinatesTextView = itemView.findViewById(R.id.text_place_coordinates);

        }

        public void fillFields(String placeName, double latitude, double longitude) {
            placeNameTextView.setText(placeName);
            placeCoordinatesTextView.setText("[" + latitude + ", " + longitude + "]");
        }
    }
}