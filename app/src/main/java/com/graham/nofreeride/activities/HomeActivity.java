package com.graham.nofreeride.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.graham.nofreeride.R;
import com.graham.nofreeride.fragments.home.HomeFragment;
import com.graham.nofreeride.fragments.summary.SummaryFragment;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener {

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
    public void onStopDrivePressed(ArrayList<LatLng> latLngs, double distance) {
        // parse locations array and start new fragment
//        ParcelableLocations parcelableLocations = new ParcelableLocations(locations);

//        ArrayList<LatLng> locations1 = new ArrayList<>();
//        locations1.add(new LatLng(25.76, -80.1918));
//        locations1.add(new LatLng(26.76, -80.1918));
//        locations1.add(new LatLng(27.76, -80.1918));
//        locations1.add(new LatLng(28.76, -80.1918));
//        locations1.add(new LatLng(29.76, -80.1918));



        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frag_container, SummaryFragment.newInstance(distance,latLngs)).addToBackStack(null).commit();

    }
}
