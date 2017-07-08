package com.example.android.wineinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
    private EditText quantityEditText;

    /**
     * Spinner to pick grape colour
     */
    private Spinner colourSpinner;

    /**
     * Wine quantity
     */
    private int wineQuantity;

    /**
     * Quantity control buttons
     */
    private Button increaseQuantity;
    private Button decreaseQuantity;

    /**
     * Wine colour. Valid values set in WineContract.
     */
    private int wineColour = WineEntry.COLOUR_UNKNOWN;

    private boolean wineHasChanged = false;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            wineHasChanged = true;
            return false;
        }
    };

    /**
     * create actions for clicking menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // what to do depending on which button is clicked
        switch (item.getItemId()) {
            // Save Wine
            case R.id.action_save_wine:
                saveWine();
                finish();
                return true;
            // delete wine
            case R.id.action_delete_wine:
                confirmDeletion();
                return true;
            // press 'back' arrow on menu
            case android.R.id.home:
                if (!wineHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, close the current activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show dialog that there are unsaved changes
                showUnsavedChangesDialogue(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


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

        //Find views to edit or read input from
        nameEditText = (EditText) findViewById(R.id.edit_wine_name);
        grapeEditText = (EditText) findViewById(R.id.edit_wine_grape);
        colourSpinner = (Spinner) findViewById(R.id.spinner_wine_colour);
        quantityEditText = (EditText) findViewById(R.id.edit_wine_quantity);
        increaseQuantity = (Button) findViewById(R.id.increas_quantity_button);
        decreaseQuantity = (Button) findViewById(R.id.decrease_quantity_button);

        //set on touch listeners to prevent accidental data loss
        nameEditText.setOnTouchListener(onTouchListener);
        grapeEditText.setOnTouchListener(onTouchListener);
        colourSpinner.setOnTouchListener(onTouchListener);
        quantityEditText.setOnTouchListener(onTouchListener);

        setupSpinnner();

        //on Click listeners for increasing or decreasing quantity
        increaseQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
                    public void onClick(View view) {
                wineQuantity += 1;
                quantityEditText.setText(Integer.toString(wineQuantity));
            }

        });

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                wineQuantity -= 1;
                if (wineQuantity < 0 ) {
                    wineQuantity = 0;
                }
                quantityEditText.setText(Integer.toString(wineQuantity));
            }

        });
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

        String nameString = nameEditText.getText().toString().trim();
        String grapeString = grapeEditText.getText().toString().trim();


        //check if values are empty and if true - return early
        if (currentWineUri == null && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(grapeString)
                && wineColour == WineEntry.COLOUR_UNKNOWN) {
            return;
        }

        // Create a ContentValues object matching column names to keys and editor fields to values
        ContentValues values = new ContentValues();
        values.put(WineEntry.COLUMN_WINE_NAME, nameString);
        values.put(WineEntry.COLUMN_WINE_GRAPE, grapeString);
        values.put(WineEntry.COLUMN_WINE_QUANTITY, wineQuantity);
        values.put(WineEntry.COLUMN_WINE_COLOUR, wineColour);

        // Check if editing existing wine or creating a new one
        if (currentWineUri != null) {
            int rowsAffected = getContentResolver().update(currentWineUri, values, null, null);
            if (rowsAffected != 0) {
                Toast.makeText(this, R.string.wine_edit_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.wine_edit_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri newUri = getContentResolver().insert(WineEntry.CONTENT_URI, values);

            if (newUri != null) {
                Toast.makeText(this, R.string.wine_edit_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.wine_edit_unsuccessful, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Method for deleting wine
     */
    private void deleteWine() {

        if (currentWineUri != null) {
            int rowsDeleted = getContentResolver().delete(currentWineUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, (R.string.wine_delete_unsuccessful), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, (R.string.wine_delete_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    /**
     * Preliminary method to launch when user clicks 'delete' to give opportunity for the user to confirm.
     * If confirmed, proceed to delete row(s) from database
     */
    private void confirmDeletion() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_warning_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            // Deletion confirmed
            public void onClick(DialogInterface dialog, int id) {
                deleteWine();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            // Deletion cancelled
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * method for warning user hasn't saved data before exiting editor
     */
    private void showUnsavedChangesDialogue(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard_changes, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * warn the user if there are unsaved changes and they have pressed the back button
     */
    @Override
    public void onBackPressed() {
        if (!wineHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialogue(discardButtonClickListener);
    }

    /**
     * Inflate toolbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Hide 'delete' from menu if creating a new wine entry
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentWineUri == null) {
            MenuItem delete = menu.findItem(R.id.action_delete_wine);
            delete.setVisible(false);
        }
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // projection to show table data required for editor view
        String[] projection = {
                WineEntry._ID,
                WineEntry.COLUMN_WINE_NAME,
                WineEntry.COLUMN_WINE_GRAPE,
                WineEntry.COLUMN_WINE_QUANTITY,
                WineEntry.COLUMN_WINE_COLOUR};
        // run on background thread
        return new CursorLoader(this, currentWineUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // move to the first row in cursor
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(WineEntry.COLUMN_WINE_NAME);
            int grapeColumnIndex = cursor.getColumnIndex(WineEntry.COLUMN_WINE_GRAPE);
            int colourColumnIndex = cursor.getColumnIndex(WineEntry.COLUMN_WINE_COLOUR);
            int quantityColumnIndex = cursor.getColumnIndex(WineEntry.COLUMN_WINE_QUANTITY);

            // Extract values to replace edit placeholders
            String name = cursor.getString(nameColumnIndex);
            String grape = cursor.getString(grapeColumnIndex);
            int colour = cursor.getInt(colourColumnIndex);
            wineQuantity = cursor.getInt(quantityColumnIndex);

            // replace placeholders
            nameEditText.setText(name);
            grapeEditText.setText(grape);
            quantityEditText.setText(Integer.toString(wineQuantity));


            // map the constant value for colour against the dropdown options, and display accordingly
            switch (colour) {
                case WineEntry.COLOUR_RED:
                    colourSpinner.setSelection(1);
                    break;
                case WineEntry.COLOUR_WHITE:
                    colourSpinner.setSelection(2);
                    break;
                case WineEntry.COLOUR_ROSE:
                    colourSpinner.setSelection(3);
                    break;
                default:
                    colourSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
