package com.example.android.wineinventory;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.wineinventory.data.WineContract.WineEntry;



public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int WINE_LOADER = 0;

    // adapter to display the list of wines
    WineCursorAdapter wineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB onClickListener to open wine editor
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the wine data
        ListView wineListView = (ListView) findViewById(R.id.list_view);

        // Find and set empty view on the ListView, to be shown when the list is empty.
        View emptyView = findViewById(R.id.empty_view);
        wineListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of wine data in the Cursor.
        //There is no data until the loader finishes, so pass in 'null', to be replaced on completion.
        wineAdapter = new WineCursorAdapter(this, null);

        // Attach the adapter to the ListView.
        wineListView.setAdapter(wineAdapter);

        // setup item click listener, to edit existing wines
        wineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent editView = new Intent(InventoryActivity.this, EditorActivity.class);
                Uri wineUri = ContentUris.withAppendedId(WineEntry.CONTENT_URI, id);
                editView.setData(wineUri);
                startActivity(editView);
            }
        });

        // Connect to existing, or create new Loader
        getLoaderManager().initLoader(WINE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
// projection to show table data required for Inventory view
        String[] projection = {
                WineEntry._ID,
                WineEntry.COLUMN_WINE_NAME,
                WineEntry.COLUMN_WINE_GRAPE,
                WineEntry.COLUMN_WINE_COLOUR};
        // run on background thread
        return new CursorLoader(this, WineEntry.CONTENT_URI, projection, null, null, null);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        wineAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        wineAdapter.swapCursor(null);

    }
}



