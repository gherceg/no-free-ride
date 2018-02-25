package com.graham.nofreeride.Home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.graham.nofreeride.Model.CompletionBlock;
import com.graham.nofreeride.Model.LocationTracker;
import com.graham.nofreeride.Model.RideCalculator;
import com.graham.nofreeride.R;
import com.graham.nofreeride.utilities.NetworkUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/2/18.
 */

public class HomeController implements HomeContract.controller {
    HomeContract.view view;

    Context context;

    private LocationTracker locationTracker;

//    private ArrayList<Location> locations;

    private double mInsurancePrice;
    private double mMPG;
    private double mPPG;

    SharedPreferences sharedPreferences;

    private boolean isDriving = false;

    protected boolean isDriving() {
        return isDriving;
    }


    public HomeController(Context context, HomeContract.view view, FusedLocationProviderClient client) {
        this.context = context;
        this.view = view;
        this.locationTracker = new LocationTracker(context, client);
    }


    public void onStartDrivingButtonPressed(String insurance, String mpg, String ppg) {

        boolean check = verifyInputs(insurance, mpg, ppg);
        if (check) {
            if (isDriving) {
                stopDrive();
            } else {
                startDrive();
            }
        } else {
            view.showInvalidInputsToast();
        }
    }

    private boolean verifyInputs(String insurance, String mpg, String ppg) {
        if (insurance.equals("-") && mpg.equals("-") && ppg.equals("-")) {
            return false;
        }
        return true;
    }

    private void startDrive() {
        // tell the view the drive has started
        view.driveHasStarted();
        isDriving = true;

        // TODO: should start tracking, receiving location updates on an interval
        locationTracker.startLocationUpdates();
//        getCurrentLocation();
    }


    private void stopDrive() {
        view.driveHasEnded();
        isDriving = false;
        // stop tracking and receive array of locations
        ArrayList<Location> locations = locationTracker.stopLocationUpdates();
        double distance = calculateDistance(locations);
        view.displayDistance(distance);
    }

    private void getUpdatedDriveInfo() {
        ArrayList<Location> locations = locationTracker.getCurrentLocationArray();
        double distance = calculateDistance(locations);
        view.displayDistance(distance);
        view.updateDriveNotifcation(distance);
    }


    public void getCurrentLocation() {
        boolean permission = locationTracker.getCurrentLocation(new CompletionBlock() {
            @Override
            public void returnLocation(Location location) {
                // not implementing this right now
            }
        });
        if (!permission) {
            view.showPermissionRequiredToast();
        }
    }

    // Helpers to calculate

    private double calculateDistance(ArrayList<Location> locations) {
        double totalDistance = 0;
        for(int i = 0; i < locations.size() - 1; i++) {
            if(locations.get(i) == null || locations.get(i+1) == null) {
                Log.d(TAG, "calculateDistance: Location at " + i + " is null");
            } else {
                totalDistance += RideCalculator.calculateDistance(locations.get(i), locations.get(i+1));
            }
        }
        return totalDistance;
    }


}
