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

        int cityNameColumnIndex = mCursor.getColumnIndex(PlaceEntry.COLUMN_CITY_NAME);
        final String cityName = mCursor.getString(cityNameColumnIndex);
        placeViewHolder.fillFields(cityName);

        placeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent weatherIntent = new Intent(mContext, WeatherActivity.class);
                weatherIntent.putExtra("cityName", cityName);
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

        private TextView cityNameTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            cityNameTextView = itemView.findViewById(R.id.places_city_name);

        }

        public void fillFields(String cityName) {
            cityNameTextView.setText(cityName);
        }
    }
}