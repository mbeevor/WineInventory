package com.example.android.wineinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.wineinventory.data.WineContract.WineEntry;


/**
 * Created by Matthew on 06/07/2017.
 */

public class WineProvider extends ContentProvider {

    /**
     * URI matcher code for the wines table; and a single wine
     */
    private static final int WINES = 100;
    private static final int WINE_ID = 101;

    /**
     * UriMatcher object used to match a content URI to a matcher code
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Static initialiser run the first time anything is called from the WineProvider
     */
    static {
        uriMatcher.addURI(WineContract.CONTENT_AUTHORITY, WineContract.PATH_WINES, WINES);
        uriMatcher.addURI(WineContract.CONTENT_AUTHORITY, WineContract.PATH_WINES + "#", WINE_ID);
    }

    private WineDatabaseHelper wineDatabaseHelper;

    /**
     * Initialise the helper
     */
    @Override
    public boolean onCreate() {
        wineDatabaseHelper = new WineDatabaseHelper(getContext());
        return true;
    }

    /**
     * perform query on given URI
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     */

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = wineDatabaseHelper.getReadableDatabase();

        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match) {

            case WINES:
                cursor = database.query(WineEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case WINE_ID:
                selection = WineEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(WineEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        /**
         * set notifcation URI on the cursor so that the cursor will update if the data changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WINES:
                return WineEntry.CONTENT_LIST_TYPE;
            case WINE_ID:
                return WineEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WINES:
                return insertWine(uri, values);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    /**
     * method to create new wine
     */

    private Uri insertWine(Uri uri, ContentValues values) {

        // Sanity check that the wine name has been entered
        String name = values.getAsString(WineEntry.COLUMN_WINE_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Wine name required");
        }

        // Sanity check that the wine grape has been entered
        String grape = values.getAsString(WineEntry.COLUMN_WINE_GRAPE);
        if (grape == null || grape.length() == 0) {
            throw new IllegalArgumentException("Grape required");
        }

        // Sanity check that wine colour has been set, or set to unknown
        Integer colour = values.getAsInteger(WineEntry.COLUMN_WINE_COLOUR);
        if (colour == null || !WineEntry.isValidColour(colour)) {
            throw new IllegalArgumentException("Please pick a wine colour");
        }

        // access writeable database
        SQLiteDatabase database = wineDatabaseHelper.getWritableDatabase();

        // insert a new row into database for a new wine, return wine ID
        long id = database.insert(WineEntry.TABLE_NAME, null, values);

        // notify listener that data has changed, and cursor needs to be updated
        getContext().getContentResolver().notifyChange(uri, null);

        // return new URI ID for new wine
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // number of rows to be deleted
        int rowsDeleted;

        // access writeable database
        SQLiteDatabase database = wineDatabaseHelper.getWritableDatabase();

        // notify listener that data has changed, and cursor needs to be updated
        getContext().getContentResolver().notifyChange(uri, null);

        final int match = uriMatcher.match(uri);
        switch (match) {

            case WINES:
                rowsDeleted = database.delete(WineEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;

            case WINE_ID:
                selection = WineEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(WineEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
                return rowsDeleted;

            default:
                throw new IllegalArgumentException("Unable to delete wines");

        }


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case WINES:
                return updateWine(uri, values, selection, selectionArgs);
            case WINE_ID:
                selection = WineEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateWine(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);
        }
    }

    /**
     * Updte wines in the database with the given content values. Only apply changes to rows specified.
     * Return number of rows updated.
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return rowsUpdated
     */

    private int updateWine(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check each field is populated and return those values to display
        if (values.containsKey(WineEntry.COLUMN_WINE_NAME)) {
            String name = values.getAsString(WineEntry.COLUMN_WINE_NAME);
            if (name == null || name.length() == 0) {
                throw new IllegalArgumentException("Wine name required");
            }
        }

        if (values.containsKey(WineEntry.COLUMN_WINE_GRAPE)) {
            String grape = values.getAsString(WineEntry.COLUMN_WINE_GRAPE);
            if (grape == null || grape.length() == 0) {
                throw new IllegalArgumentException("Grape required");
            }
        }

        if (values.containsKey(WineEntry.COLUMN_WINE_COLOUR)) {
            Integer colour = values.getAsInteger(WineEntry.COLUMN_WINE_COLOUR);
            if (colour == null || !WineEntry.isValidColour(colour)) {
                throw new IllegalArgumentException("Please pick a wine colour");
            }
        }

        // if there are no values to update, don't update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, access writeable database
        SQLiteDatabase database = wineDatabaseHelper.getWritableDatabase();

       int rowsUpdated = database.update(WineEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            // notify listener that data has changed, and cursor needs to be updated
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // return the number of rows updated
        return rowsUpdated;
    }
}
