package com.example.android.wineinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Wine App
 */

public final class WineContract {

    //constants for content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.wineinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WINES = "wines";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private WineContract() {}

    /**
     * Inner class that defines constant values for the wine database table.
     * Each entry in the table represents a single bottle of wine.
     */
    public static final class WineEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_WINES);

        /**
         * The MIME type for the full list of wines
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WINES;

        /**
         * The MIME type for a single wine
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WINES;

        /**
         * Name of database table for wines
         */
        public static final String TABLE_NAME = "wines";

        /**
         * Unique ID for each wine (Type: INTEGER)
         */
        public static final String _ID = BaseColumns._ID;

        /**
        * Name of each wine (Type: STRING)
        */
        public static final String COLUMN_WINE_NAME = "name";

        /**
         * Body of each wine (Type: STRING)
         */
        public static final String COLUMN_WINE_GRAPE = "grape";

        /**
         * Price of the wine (Type: INTEGER)
         */
        public static final String COLUMN_WINE_PRICE = "price";

        /**
         * Colour of the wine (Type: INTEGER)
         */
        public static final String COLUMN_WINE_COLOUR = "colour";

        /**
         * Quantity remaining (Type: INTEGER)
         */
        public static final String COLUMN_WINE_QUANTITY = "quantity";


        /**
         * Returns whether or not the wine colour is unknown, red, white or rose.
         */
        public static boolean isValidColour(int colour) {
            if (colour == COLOUR_UNKNOWN || colour == COLOUR_RED || colour == COLOUR_WHITE
                    || colour == COLOUR_ROSE ) {
                return true;
            }
            return false;
        }


        /**
         * Possible values for wine colour
         */
        public static final int COLOUR_UNKNOWN = 0;
        public static final int COLOUR_RED = 1;
        public static final int COLOUR_WHITE = 2;
        public static final int COLOUR_ROSE = 3;

    }

}
