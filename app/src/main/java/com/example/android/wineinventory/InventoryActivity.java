package com.example.android.wineinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;


public class InventoryActivity extends AppCompatActivity {

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
                Intent editWine = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(editWine);
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
    }

}



