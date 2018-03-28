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

public class HomeController {

    HomeContract.view view;

    Context context;


    public boolean driveInProgress = false;

    public boolean isDriveInProgress() {
        return driveInProgress;
    }

    public void setDriveInProgress(boolean driveInProgress) {
        this.driveInProgress = driveInProgress;
    }


    /**
     * Constructor for the HomeController class
     * @param context - TODO: used when LocationTracking was performed within the controller (may not be needed anymore)
     * @param view - Interface to communicate with the fragment
     */
    public HomeController(Context context, HomeContract.view view ) {
        this.context = context;
        this.view = view;
    }


    /**
     * This method handles logic for when start drive button is pressed on the view
     * @param insurance - current set insurance price
     * @param mpg - current mpg for car
     * @param ppg - current price per gallon
     */
    public void onDriveButtonPressed(String insurance, String mpg, String ppg) {

        boolean check = verifyInputs(insurance, mpg, ppg);
        if (check) {
            if (driveInProgress) {
                stopDrive();
            } else {
                startDrive();
            }
        } else {
            view.showInvalidInputsToast();
        }
    }

    /**
     *
     * @param insurance
     * @param mpg
     * @param ppg
     * @return
     */
    private boolean verifyInputs(String insurance, String mpg, String ppg) {
        if (insurance.equals("-") && mpg.equals("-") && ppg.equals("-")) {
            return false;
        }
        return true;
    }

    /**
     * This method handles what to do when starting a drive
     */
    private void startDrive() {
        // update drive flag
        driveInProgress = true;
        // tell view that the drive should be started (which will tell the activity)
        // TODO: could this be improved???
        view.startDrive();
    }

    /**
     * This method handles what to do when stopping a drive
     */
    private void stopDrive() {
        // update drive flag
        driveInProgress = false;
        // uses a service
        view.stopDrive();
    }

}
