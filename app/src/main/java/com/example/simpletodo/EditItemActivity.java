package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.simpletodo.MainActivity.ITEM_POSITION;
import static com.example.simpletodo.MainActivity.ITEM_TEXT;

public class EditItemActivity extends AppCompatActivity {

    // text field containing updated item description
    EditText etItemText;
    // we need to track the item's position in the list
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // resolve the text field from the layout
        etItemText = (EditText) findViewById(R.id.etItemText);
        // set the text field's content from the intent
        etItemText.setText(getIntent().getStringExtra(ITEM_TEXT));
        // track the position of the item in the list
        position = getIntent().getIntExtra(ITEM_POSITION, 0);
        // set the title bar to reflect the purpose of the view
        getSupportActionBar().setTitle("Edit item");
    }

    public void onSaveItem(View v) {
        // Prepare intent to pass back to MainActivity
        Intent data = new Intent();
        // Pass updated item text and original position
        data.putExtra(ITEM_TEXT, etItemText.getText().toString());
        data.putExtra(ITEM_POSITION, position);
        setResult(RESULT_OK, data); // set result code and bundle data for the response
        finish();
    }
}
