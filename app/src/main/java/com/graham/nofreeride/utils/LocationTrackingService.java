package com.graham.nofreeride.utils;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/25/18.
 */

public class LocationTrackingService extends Service {

    // 15 second update interval
    private static final long LOCATION_UPDATE_INTERVAL_MS = 15 * 1000;
    // We will take a location update every 5 seconds if it is available
    private static final long FASTEST_LOCATION_UPDATE_INTERVAL_MS = 5 * 1000;

    // thread stuff
    private Looper mServiceLooper;
//    private ServiceHandler mServiceHandler;

    //    private LocationListener listener;
    private LocationManager locationManager;
    private FusedLocationProviderClient mLocationProvider;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private Location mLastLocation;
    private ArrayList<Location> mLocations;

    // NOTE: Code snippet for local binder found here
    // https://medium.com/@ankit_aggarwal/ways-to-communicate-between-activity-and-service-6a8f07275297
    // -----
//    private final IBinder mBinder = new LocalBinder();

//    public class LocalBinder extends Binder {
//        LocationTrackingService getService() {
//            return LocationTrackingService.this;
//        }
//    }
    // ------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start location updates
        Log.d(TAG, "onStartCommand: Starting Location Tracking Service");
//        mLocationProvider.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        return Service.START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Create new thread to handle tracking location
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();


        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        // init locations array
        mLocations = new ArrayList<>();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        // setup location request
        setupLocationRequest();
        //
        getLastLocation();

    }


    /**
     * Set up the LocationRequest object (interval time, priority)
     */
    private void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL_MS);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATE_INTERVAL_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getLastLocation() {
        try {
            mLocationProvider.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() != null) {
                                mLastLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get last location.");
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Location Permission not granted. Exception: " + e);
        }
    }

    private void onNewLocation(Location lastLocation) {
        // add this location to our array of locations
        if(lastLocation != null)
            mLocations.add(lastLocation);
        else
            Log.w(TAG,"Location is not valid to add to locations array");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Stopping Location Tracking Service");
        mLocationProvider.removeLocationUpdates(mLocationCallback);

        // setup intent to pass in broadcast receiver

    }


}
