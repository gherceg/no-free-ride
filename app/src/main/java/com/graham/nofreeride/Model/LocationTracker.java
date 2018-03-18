package com.graham.nofreeride.Model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/3/18.
 */

public class LocationTracker {

    FusedLocationProviderClient locationProviderClient;
    Context context;

    LocationRequest locationRequest;
    LocationCallback locationCallback;


    private ArrayList<Location> locationList;
    private ArrayList<LatLng> latLngList;
    private Location mStartingLocation;
    private Location mEndingLocation;

    /**
     * Constructor for the location tracker
     * @param context - provide a context since we are outside of the Activity scope
     * @param locationProviderClient - DI, give the tracking object its locationProvider
     */
    public LocationTracker(Context context, FusedLocationProviderClient locationProviderClient) {
        this.locationProviderClient = locationProviderClient;
        this.context = context;

        // initialize locations array
        locationList = new ArrayList<>();
        latLngList = new ArrayList<>();

        // setup location services
        locationRequest = new LocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()) {
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    latLngList.add(latLng);
                    locationList.add(location);
                    String loc = location.toString();
                    String message = String.format("Location is %s", loc);
                    Log.d(TAG, message);
                }
            }
        };
    }

    /**
     * Used to get make a getLastLocation call to the location provider
     * @param completionBlock so that caller is informed when a location has successfully been retrieved
     * @return flag to determine if permission has been granted
     */
    public boolean getCurrentLocation(final CompletionBlock completionBlock) {
        boolean permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permission) {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            completionBlock.returnLocation(location);
                        }
                    });
        }
        else {
            Log.e(TAG, "Permission was denied! Unable to retrieve location.");
        }
        return permission;
    }


    /**
     * Called to start grabbing locations
     * NOTE: time interval to grab new location is set within this method for now
     * @return a flag to determine if permission has been granted or not by the user
     */
    public boolean startLocationUpdates() {
        boolean permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permission) {
            // create a Pending Intent
            locationRequest.setInterval(1000); // ms?
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
        }
        else {
            Log.e(TAG, "Permission was denied! Unable to retrieve location.");
        }
        return permission;
    }

    /**
     * Called to stop receiving location updates
     * @return an array of Location objects collected over the course of the collection period
     */
    public ArrayList<Location> stopLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationCallback);
        return locationList;
    }

    public ArrayList<Location> getCurrentLocationArray() {
        return locationList;
    }

    public ArrayList<LatLng> getCurrentLatLngArray() {
        return latLngList;
    }
}
