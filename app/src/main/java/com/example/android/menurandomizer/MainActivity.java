package com.example.android.menurandomizer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.menurandomizer.data.HomeMenuContract;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MENU_LOADER_ID = 0;
    private TextView randomOutput;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView, mRecyclerViewMainDishes, mRecyclerViewGarnish, mRecyclerViewSoups, mRecyclerViewSalads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewDishes);
        mRecyclerViewMainDishes = (RecyclerView) findViewById(R.id.recyclerViewMainDishes);
        mRecyclerViewGarnish = (RecyclerView) findViewById(R.id.recyclerViewGarnish);
        mRecyclerViewSoups = (RecyclerView) findViewById(R.id.recyclerViewSoup);
        mRecyclerViewSalads = (RecyclerView) findViewById(R.id.recyclerViewSalad);

        randomOutput = (TextView) findViewById(R.id.randomize_output);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMainDishes.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewSoups.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewGarnish.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewSalads.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        ItemTouchHelper swipToDelete = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // URI for the item to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = HomeMenuContract.DishEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                //Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                //Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(MENU_LOADER_ID, null, MainActivity.this);

            }
        });
        swipToDelete.attachToRecyclerView(mRecyclerView);
        swipToDelete.attachToRecyclerView(mRecyclerViewMainDishes);
        swipToDelete.attachToRecyclerView(mRecyclerViewSoups);
        swipToDelete.attachToRecyclerView(mRecyclerViewGarnish);
        swipToDelete.attachToRecyclerView(mRecyclerViewSalads);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.addDishBtn);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addDishIntent = new Intent(MainActivity.this, AddDishActivity.class);
                startActivity(addDishIntent);
            }
        });

        final Button randomizeBtn = (Button) findViewById(R.id.randomize_btn);

        randomizeBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                randomizeBtn.setText(R.string.random_btn_2);
                                                randomOutput.setText(randomize());
                                            }
                                        });
        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(MENU_LOADER_ID, null, this);
    }

    protected String randomize(){
        Cursor cursortoAllDishes = getContentResolver().query(HomeMenuContract.DishEntry.CONTENT_URI,
                null,
                null,
                null,
                HomeMenuContract.DishEntry.COLUMN_DISHTYPE);

        if (cursortoAllDishes !=null) {
            int numberOfDishes = cursortoAllDishes.getCount();
            Random randomizer = new Random();
            int randomNumber = randomizer.nextInt(numberOfDishes);
            System.out.println("randomNumber: " + randomNumber);
            for (int i=0;i<randomNumber;i++){
                if(!cursortoAllDishes.moveToNext()){
                    cursortoAllDishes.moveToFirst();
                }
            }
            cursortoAllDishes.moveToNext();
            int descriptionIndex = cursortoAllDishes.getColumnIndex(HomeMenuContract.DishEntry.COLUMN_DESCRIPTION);
            int idIndex = cursortoAllDishes.getColumnIndex(HomeMenuContract.DishEntry._ID);
            int id = cursortoAllDishes.getInt(idIndex);
            String dishDescription = cursortoAllDishes.getString(descriptionIndex);
            System.out.println("RANDOMIZED: " + dishDescription + " mit der ID: " + id);
            return dishDescription;
        } else {
            return "No data has been found in the database";
        }
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(MENU_LOADER_ID, null, this);
    }

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mDishData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mDishData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mDishData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Query and load all task data in the background; sort by priority
                try {
                    return getContentResolver().query(HomeMenuContract.DishEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            HomeMenuContract.DishEntry.COLUMN_DISHTYPE);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mDishData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}

