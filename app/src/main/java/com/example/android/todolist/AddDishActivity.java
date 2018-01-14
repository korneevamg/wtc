/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.todolist.data.HomeMenuContract;


public class AddDishActivity extends AppCompatActivity {

    private static final String TAG = AddDishActivity.class.getSimpleName();
    // Variable to keep track of selected type of the dish
    private String dishType;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        // Initialize as Main Dish by default
        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        dishType = "Main Dish";
    }


    /**
     * onClickAddDish is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddDish(View view) {
        // Not yet implemented
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        String input = ((EditText) findViewById(R.id.editDishName)).getText().toString();
        if (input.length() == 0) {
            return;
        }

        // Insert new task data via a ContentResolver
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        // Put the dish description and type into the ContentValues
        contentValues.put(HomeMenuContract.DishEntry.COLUMN_DESCRIPTION, input);
        contentValues.put(HomeMenuContract.DishEntry.COLUMN_DISHTYPE, dishType);
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(HomeMenuContract.DishEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }

        // Finish activity (this returns back to MainActivity)
        finish();

    }


    /**
     * onPrioritySelected is called whenever a dish type button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onDishTypeSelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            dishType = "Soup";
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            dishType = "Main Dish";
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            dishType = "Salad";
        } else if (((RadioButton) findViewById(R.id.radButton4)).isChecked()) {
            dishType = "Garnish";
        }
    }
}
