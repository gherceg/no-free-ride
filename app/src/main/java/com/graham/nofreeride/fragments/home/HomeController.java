package com.graham.nofreeride.fragments.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.graham.nofreeride.Model.CompletionBlock;
import com.graham.nofreeride.Model.LocationTracker;
import com.graham.nofreeride.utils.RideCalculator;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/2/18.
 */

public class HomeController implements HomeContract.controller {

    HomeContract.view view;

    Context context;

    private LocationTracker locationTracker;

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
    }


    private void stopDrive() {
        view.driveHasEnded();
        isDriving = false;
        // stop tracking and receive array of locations
        ArrayList<Location> locations = locationTracker.stopLocationUpdates();
        ArrayList<LatLng> latLngs = locationTracker.getCurrentLatLngArray();
        double distance = calculateDistance(latLngs);
        view.displaySummaryPage(latLngs,distance);
    }


    private void getUpdatedDriveInfo() {
        ArrayList<LatLng> latLngs = locationTracker.getCurrentLatLngArray();
        double distance = calculateDistance(latLngs);
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
    private double calculateDistance(ArrayList<LatLng> latLngs) {
        double totalDistance = 0;
        for(int i = 0; i < latLngs.size() - 1; i++) {
            if(latLngs.get(i) == null || latLngs.get(i+1) == null) {
                Log.d(TAG, "calculateDistance: Location at " + i + " is null");
            } else {
                totalDistance += RideCalculator.calculateDistance(latLngs.get(i), latLngs.get(i+1));
            }
        }
        return totalDistance;
    }


}
