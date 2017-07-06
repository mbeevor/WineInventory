package com.example.android.wineinventory;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.wineinventory.data.WineContract.WineEntry;

/**
 * Created by Matthew on 06/07/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_WINE_LOADER = 0;

    /**
     * Content URI for the existing wine, 'null' if it is a new wine
     */
    private Uri currentWineUri;

    /**
     * EditText fields to enter the Wine details
     */
    private EditText nameEditText;
    private EditText grapeEditText;

    /**
     * Spinner to pick grape colour
     */
    private Spinner colourSpinner;

    /**
     * Wine colour. Valid values set in WineContract.
     */
    private int wineColour = WineEntry.COLOUR_UNKNOWN;
    private boolean colourHasChanged = false;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            colourHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // check if editing an existing wine, or creating a new one
        Intent editWine = getIntent();
        currentWineUri = editWine.getData();

        // create new wine if there is no URI
        if (currentWineUri == null) {
            setTitle(getString(R.string.new_wine_editor_title));
            invalidateOptionsMenu();
        } else {
            // edit existing wine
            setTitle(getString(R.string.existing_wine_editor_title));
            getLoaderManager().initLoader(EXISTING_WINE_LOADER, null, this);
        }

        /**
         * Find views to edit
         */
        nameEditText = (EditText) findViewById(R.id.edit_wine_name);
        grapeEditText = (EditText) findViewById(R.id.edit_wine_grape);
        colourSpinner = (Spinner) findViewById(R.id.spinner_wine_colour);

        /**
         * set on touch listeners to prevent accidental data loss
         */
        nameEditText.setOnTouchListener(onTouchListener);
        grapeEditText.setOnTouchListener(onTouchListener);
        colourSpinner.setOnTouchListener(onTouchListener);

        setupSpinnner();
    }

    /**
     * Set up spinner that allows user to pick the wine colour; default set to unknown
     */


    private void setupSpinnner() {

        ArrayAdapter colourSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_wine_colour, android.R.layout.simple_spinner_item);
        colourSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourSpinner.setAdapter(colourSpinnerAdapter);

        // set integer selected to constant values
        colourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.colour_red))) {
                        wineColour = WineEntry.COLOUR_RED;
                    } else if (selection.equals(getString(R.string.colour_white))) {
                        wineColour = WineEntry.COLOUR_WHITE;
                    } else if (selection.equals(getString(R.string.colour_rose))) {
                        wineColour = WineEntry.COLOUR_ROSE;
                    } else {
                        wineColour = WineEntry.COLOUR_UNKNOWN;
                    }
                }
            }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    wineColour = WineEntry.COLOUR_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save new wine into database
     */
    private void saveWine() {

        String name = nameEditText.getText().toString().trim();
        String grape = grapeEditText.getText().toString().trim();

        //check if values are empty and if true - return early
        if (currentWineUri == null && TextUtils.isEmpty(name)
                && TextUtils.isEmpty(grape)
                && wineColour == WineEntry.COLOUR_UNKNOWN) {
            return;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
