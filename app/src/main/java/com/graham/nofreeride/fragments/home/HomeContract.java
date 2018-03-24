package com.graham.nofreeride.fragments.home;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by grahamherceg on 2/2/18.
 */

public interface HomeContract {

    interface view {


        void showInvalidInputsToast();

        void driveHasStarted();
        void startDriveUsingService();
        void driveHasEnded();
        void endDrive();

        void showPermissionRequiredToast();

        void displaySummaryPage(double distance);

        void displaySummaryPage(ArrayList<LatLng> latLngs, double distance);

        void updateDriveNotifcation(double distance);

    }

    interface controller {
    }
}
