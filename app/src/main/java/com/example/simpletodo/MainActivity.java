package com.example.simpletodo;
// each full screen in an app is a separate Activity and each has at least
// 2 parts - the Java source files and the XML layout
// Each activity is independent and does not directly communicate with another

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    // declaring constants to be used when passing data
    // these fields don't belong to the activity and go all the way to the root

    // a numeric code to identify the edit activity
    public static final int EDIT_REQUEST_CODE = 20;
    // keys used for passing data between activities
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";


    //psf shortcut to produce constant definitions and should typically be kept as primitives

    // declare fields here
    ArrayList<String> items; //Dynamic arrays
    ArrayAdapter<String> itemsAdapter; // convert the array list items into a views for display within Android apps - Strings are adapted for the app's listview
    ListView lvItems; // accepts an adapter

    // text field containing updated item description
    EditText etItemText;
    // to track the item's position in the list
    int position;


    // the @ symbol is an annotation which can either be a signal to the compiler or code generations
    @Override
    // called by Android when the activity is created
    protected void onCreate(Bundle savedInstanceState) {
        // savedInstanceState is the primary that the OS gives
        // will always be null when being called for the first time and therefore when being re-run, the properties can be recovered and the view adjusted accordingly
        super.onCreate(savedInstanceState);
        // inflating the layout file - activity_main.xml
        setContentView(R.layout.activity_main); // finds the layout and inflates it - takes the XML and turns it to a Java object and places it on the screen

        etItemText = (EditText) findViewById(R.id.etNewItem);
        etItemText.setText(getIntent().getStringExtra(ITEM_TEXT));
        position = getIntent().getIntExtra(ITEM_POSITION, 0);
        getSupportActionBar().setTitle("Edit Item");

        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items); // takes the list of items and wires it up to the view which was developed by Google
        // this refers to this instantiation of MainActivity (generally, the nearest containing class) - which is why the activity must be specified when inside other methods and functions
        lvItems.setAdapter(itemsAdapter);

        //setup the deletion/long click listener on creation
        setupListViewListener();
    }

    // TODO - Place in a separate document
    // With the rotation of your phone or anything else considered to be a configuration change will kill the current activity and re-run onCreate

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem); // knows how to restore state as long as it's associated with an id
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        writeItems();
        etNewItem.setText(" ");
        // display a notification to the user
        Toast.makeText(getApplicationContext(), "Item added to list", Toast.LENGTH_SHORT).show();
    }

    public void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                Log.i("MainActivity", "Removed Item " + position);
                return true;
            }
        });

        // set the ListView's regular click listener
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // the first parameter is the context, second is the class of the activity to launch
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                // put 'extras' into the bundle for access in the edit activity
                i.putExtra(ITEM_TEXT, items.get(position));
                i.putExtra(ITEM_POSITION, position);
                // brings up the edit activity with the expectation of a result
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });
    }

    // methods to support persistence

    // returns the file in which the data is stored
    private File getDataFile() {
        return new File(getFilesDir(), "todo.txt");
    }

    // read the items from the file system
    private void readItems() {
        try {
            items = new ArrayList<String>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
            items = new ArrayList<>();
        }
    }

    // write the items to the file system
    private void writeItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSaveItem(View v) {
        Intent data = new Intent();
        data.putExtra(ITEM_TEXT, etItemText.getText().toString());
        data.putExtra(ITEM_POSITION, position);
        setResult(RESULT_OK, data);
        finish();
    }

    // overriding in order to intercept the result, and update the model


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // EDIT_REQUEST_CODE defined within constants
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            // extract updated item value from result extras
            String updatedItem = data.getExtras().getString(ITEM_TEXT);
            // get the position of the item which was edited
            int position = data.getExtras().getInt(ITEM_POSITION, 0);
            // update the model with the new item at the edited position
            items.set(position, updatedItem);
            // notify the adapter the model changed
            itemsAdapter.notifyDataSetChanged();
            // Store the updated items back to disk
            writeItems();
            // notify the user the operation completed OK
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }
}
