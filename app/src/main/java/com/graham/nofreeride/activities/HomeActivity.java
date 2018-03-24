package com.graham.nofreeride.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.graham.nofreeride.R;
import com.graham.nofreeride.fragments.home.HomeFragment;
import com.graham.nofreeride.fragments.summary.SummaryFragment;
import com.graham.nofreeride.utils.Constants;
import com.graham.nofreeride.utils.LocationTrackingService;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener, SummaryFragment.SummaryFragmentListener {
    public static final String LOG_TAG = "Home Activity";

    HomeFragment homeFragment;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        // get the home fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frag_container,new HomeFragment())
                .commit();


        // register to receive intents named "custom-event-name"
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, new IntentFilter(Constants.ACTION.SENDLOCATIONS_ACTION));
    }

    private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        public static final String TAG = "Broadcast Receiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constants.ACTION.SENDLOCATIONS_ACTION)) {
                Log.d(TAG, "onReceive: locations were received by activity");
//                    Toast.makeText(getApplicationContext(),"Received locations",Toast.LENGTH_SHORT).show();
                ArrayList<LatLng> latLngs = intent.getParcelableArrayListExtra("locations");
                double distance = intent.getDoubleExtra("distance",0);
                showSummaryPage(latLngs,distance);
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

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

    @Override
    public void onStartDrivePressed() {
        // start location tracking service
        Intent startIntent = new Intent(HomeActivity.this, LocationTrackingService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);
//        if(!LocationTrackingService.IS_SERIVCE_RUNNING) {
////            service.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//        }
    }

    @Override
    public void onStopDrivePressed(ArrayList<LatLng> latLngs, double distance) {
        Intent stopIntent = new Intent(HomeActivity.this, LocationTrackingService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(stopIntent);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frag_container, SummaryFragment.newInstance(distance,latLngs)).addToBackStack(null).commit();
    }

    private void showSummaryPage(ArrayList<LatLng> latLngs, double distance) {
        Log.d(LOG_TAG, "showSummaryPage: trying to show summary page");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frag_container, SummaryFragment.newInstance(distance,latLngs)).addToBackStack(null).commit();
    }



    @Override
    public void onSummarySwipeUp() {
        // start detailed summary page
    }
}
