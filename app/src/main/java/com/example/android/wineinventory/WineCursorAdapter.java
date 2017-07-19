package com.example.android.wineinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wineinventory.data.WineContract.WineEntry;
import static java.lang.Integer.parseInt;

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
     *
     * @param view   Existing view, returned from newView above
     * @param cursor The Cursor from which to get the data, which is already pointing to the correct row
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find ids to populate
        final TextView name = (TextView) view.findViewById(R.id.text_wine_name);
        final TextView grape = (TextView) view.findViewById(R.id.text_wine_grape);
        final TextView quantity = (TextView) view.findViewById(R.id.text_wine_quantity);
        TextView currency = (TextView) view.findViewById(R.id.currency_symbol);
        TextView price = (TextView) view.findViewById(R.id.wine_price);
        ImageView decreaseQuantity = (ImageView) view.findViewById(R.id.sale_button);
        ImageView displayWineColour = (ImageView) view.findViewById(R.id.drawable_wine_colour);


        // Extract information from cursor
        final String wineName = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_NAME));
        String wineGrape = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_GRAPE));
        String winePrice = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_PRICE));
        final String wineQuantity = cursor.getString(cursor.getColumnIndex(WineEntry.COLUMN_WINE_QUANTITY));
        final long id = cursor.getLong(cursor.getColumnIndex(WineEntry._ID));

        // shopping basket listener; automatically decreases quantity available by one
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int wineQuantity = parseInt(quantity.getText().toString());
                wineQuantity -= 1;

                if (wineQuantity < 0 ) {
                    wineQuantity = 0;
                }
                quantity.setText(Integer.toString(wineQuantity));

                // find values to update in database
                ContentValues values = new ContentValues();
                values.put(WineEntry.COLUMN_WINE_QUANTITY, wineQuantity);

                // find current wine URI
                Uri currentWineUri = ContentUris.withAppendedId(WineEntry.CONTENT_URI, id);

                // update affected row in database
                int rowAffected = context.getContentResolver().update(currentWineUri, values, null, null);
                Log.v("WineCursor", "Number of rows affected: " + rowAffected);
                if (rowAffected != 0 && wineQuantity != 0) {
                    Toast.makeText(context, R.string.wine_sale_successful, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.wine_sale_unsuccessful, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Integer wineColour = cursor.getInt(cursor.getColumnIndex(WineEntry.COLUMN_WINE_COLOUR));

        // replace grape with placeholder text if this hasn't been completed
        if (TextUtils.isEmpty(wineGrape)) {
            wineGrape = context.getString(R.string.unknown_grape);
        }

        // change the colour shown next to listing according to colour selected in editor
        switch (wineColour) {
            case WineEntry.COLOUR_RED:
                wineColour = R.color.redWine;
                break;
            case WineEntry.COLOUR_WHITE:
                wineColour = R.color.whiteWine;
                break;
            case WineEntry.COLOUR_ROSE:
                wineColour = R.color.roseWine;
                break;
            default:
                wineColour = R.color.colorIcons;
                break;
        }

        // Populate fields with extracted properties
        name.setText(wineName);
        grape.setText(wineGrape);
        currency.setText(R.string.currency_label);
        price.setText(winePrice);
        quantity.setText(wineQuantity);
        displayWineColour.setImageResource(wineColour);

    }

}

