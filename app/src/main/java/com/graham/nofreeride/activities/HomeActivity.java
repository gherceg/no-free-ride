package com.graham.nofreeride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.graham.nofreeride.R;
import com.graham.nofreeride.fragments.detail_summary.DetailSummaryFragment;
import com.graham.nofreeride.fragments.home.HomeFragment;
import com.graham.nofreeride.fragments.summary.SummaryFragment;
import com.graham.nofreeride.utils.Constants;
import com.graham.nofreeride.services.LocationTrackingService;

import java.util.ArrayList;

/**
 * Home Activity class responsible for managing fragments, starting/stopping service, and receiving broadcasts from services
 */

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener, SummaryFragment.SummaryFragmentListener, DetailSummaryFragment.DetailSummaryFragmentListener {
    public static final String LOG_TAG = "Home Activity";

    // references to fragments (not using this now)
    HomeFragment homeFragment;
    SummaryFragment summaryFragment;
    DetailSummaryFragment detailSummaryFragment;

    Toolbar toolbar;

    // private members to pass data back and fourth from fragments
    private double mParkingCost;
    private int mNumOfPassengers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // set defaults
        mParkingCost = 0.0;
        mNumOfPassengers = 1;

        // set up toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setLogo(R.mipmap.ic_app_icon);
            getSupportActionBar().setTitle("");
        }

        // create and set the home fragment
        homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frag_container,homeFragment)
                .commit();


        // register to receive intents named "custom-event-name"
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mLocationReceiver, new IntentFilter(Constants.ACTION.SENDLOCATIONS_ACTION));
        broadcastManager.registerReceiver(mLocationReceiver, new IntentFilter(Constants.ACTION.STOPMESSAGE_ACTION));
    }


    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        public static final String TAG = "Broadcast Receiver";


        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.ACTION.SENDLOCATIONS_ACTION)) {
//                Log.d(TAG, "onReceive: locations were received by activity");
                ArrayList<LatLng> latLngs = intent.getParcelableArrayListExtra("locations");
                double distance = intent.getDoubleExtra("distance",0);
                // if locations are empty don't move to the next page
                if(latLngs.isEmpty()) {
//                    Log.d(TAG, "onReceive: no locations exist yet..not showing summary page");
                    Toast.makeText(getApplicationContext(),"Not enough data",Toast.LENGTH_SHORT).show();
                } else {
                    showSummaryPage(latLngs,distance);
                }
            }
            else if(intent.getAction().equals(Constants.ACTION.STOPMESSAGE_ACTION)) {
//                Log.d(TAG, "onReceive: received a stop message");

                // tell the home fragment to update accordingly
                homeFragment.driveStoppedExternally();
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * This method is called when activity is being destroyed.
     * Important to unregister the receiver
     */
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    /**
     * This method handles any click events on option items.
     * Only used for the menu and back button right now
     * @param item - contains information about which item was selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO: do I need both this method and onOptionsItemSelected
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * This method is called when the fragment UI receives a start drive event.
     * Responsible for starting the foreground service to track user's location.
     */
    @Override
    public void onStartDrivePressed() {
        // start location tracking service
        Intent startIntent = new Intent(HomeActivity.this, LocationTrackingService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);
    }

    /**
     * This method is called when the fragment UI receives a stop drive event.
     * Responsible for stopping the foreground service completely
     */
    @Override
    public void onStopDrivePressed() {
        Intent stopIntent = new Intent(HomeActivity.this, LocationTrackingService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(stopIntent);
    }

    /**
     * This method is called when the activity receives a broadcast message containing data from the service.
     * @param latLngs - array of latitude, longitude pairs in the order they were collected
     * @param distance - total distance traveled
     */
    private void showSummaryPage(ArrayList<LatLng> latLngs, double distance) {
//        Log.d(LOG_TAG, "showSummaryPage: trying to show summary page");
        summaryFragment = SummaryFragment.newInstance(distance,latLngs);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frag_container, summaryFragment).addToBackStack(null).commit();
    }

    // ------ Callbacks from Summary Fragment ------

    /**
     * Method to handle adding Detail Summary Fragment
     * @param distance - pass the distance to the detail summary fragment
     * @param passengers - pass the number of passengers as user may have changed it before navigating to detail page
     */
    @Override
    public void onSummarySwipeUp(double distance, int passengers) {
        // set number of passengers on activity
        setNumOfPassengers(passengers);
        // start detailed summary page
        detailSummaryFragment = DetailSummaryFragment.newInstance(distance,passengers);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frag_container, detailSummaryFragment).addToBackStack(null).commit();
    }

    // ------ Callbacks from Detail Summary Fragment ------
    /**
     * Method to handle saving parking cost added by user
     * @param parking - cost of parking added by user
     */
    @Override
    public void onParkingCostUpdated(double parking) {
        setParkingCost(parking);
    }

    /**
     * Method called when detail summary page is swiped down
     */
    @Override
    public void onDetailSummarySwipeDown(int passengers) {
        // set number of passengers on Activity
        setNumOfPassengers(passengers);

//        Log.d("Home_Activity", "onDetailSummarySwipeDown: trying to pop the stack");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();

    }

    // Getters for fragments
    public void setParkingCost(double cost) {
        mParkingCost = cost;
    }

    public double getParkingCost() {
        return mParkingCost;
    }

    public void setNumOfPassengers(int passengers) {
        mNumOfPassengers = passengers;
    }

    public int getNumOfPassengers() {
        return mNumOfPassengers;
    }
}
