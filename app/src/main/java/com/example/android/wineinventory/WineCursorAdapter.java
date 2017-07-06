package com.example.android.wineinventory;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.wineinventory.data.WineContract.WineEntry;
/**
 * The WineCursorAdapter is an adapter for a ListView that uses a Cursor of wine data.
 * This adapter knows how to create list items for each row of wine data in the Cursor.
 */

public class WineCursorAdapter extends CursorAdapter {

    public WineCursorAdapter(Context context, Cursor data) {
        super(context, data, 0);
    }

    /**
     * create a blank list item view
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * Now bind the wine data from the current row pointed at by the cursor to the listView above.
     * @param view Existing view, returned from newView above
     * @param cursor The Cursor from which to get the data, which is already pointing to the correct row
     *
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find ids to populate
        TextView name = (TextView) view.findViewById(R.id.text_wine_name);
        TextView grape = (TextView) view.findViewById(R.id.text_wine_grape);
        TextView colour = (TextView) view.findViewById(R.id.text_wine_colour);
//        TextView price = (TextView) view.findViewById(R.id.text_wine_price);
//        TextView quantity = (TextView) view.findViewById(R.id.text_wine_quantity);

        // Extract information from cursor
        String wineName = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_NAME));
        String wineGrape = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_GRAPE));
        String wineColour = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_COLOUR));
//        String winePrice = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_PRICE));
//        String wineQuantity = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_QUANTITY));

        // replace grape with placeholder text if this hasn't been completed
        if (TextUtils.isEmpty(wineGrape)) {
            wineGrape = context.getString(R.string.unknown_grape);
        }

        // Populate fields with extracted properties
        name.setText(wineName);
        grape.setText(wineGrape);
        colour.setText(wineColour);
//        price.setText(winePrice);
//        quantity.setText(wineQuantity);
    }

}

