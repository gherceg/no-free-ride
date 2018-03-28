package com.graham.nofreeride.fragments.home;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by grahamherceg on 2/2/18.
 */

/**
 * Interface to separate fragment interactions from logic
 */
public interface HomeContract {

    interface view {

        void showInvalidInputsToast();
        void startDrive();
        void stopDrive();
    }

}
