package com.example.android.wineinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.wineinventory.data.WineContract.WineEntry;

/**
 * Database helper for Wine app. Manages database creation and version control.
 */

public class WineDatabaseHelper extends SQLiteOpenHelper{

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "winecellar.db";

    /**
     * Database version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Create new instance of WineDatabaseHelper
     */
    public WineDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // string for creating the wines table
        String SQL_CREATE_TABLE = "CREATE TABLE " + WineEntry.TABLE_NAME + " ("
                + WineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WineEntry.COLUMN_WINE_NAME + " TEXT NOT NULL, "
                + WineEntry.COLUMN_WINE_GRAPE + " TEXT NOT NULL, "
                + WineEntry.COLUMN_WINE_COLOUR + " INTEGER NOT NULL, "
                + WineEntry.COLUMN_WINE_PRICE + " INTEGER NOT NULL DEFAULT 0);";

        // Execute
        database.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
