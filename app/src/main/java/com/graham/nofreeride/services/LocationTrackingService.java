package com.graham.nofreeride.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.graham.nofreeride.R;
import com.graham.nofreeride.activities.HomeActivity;
import com.graham.nofreeride.utils.Constants;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/25/18.
 */

public class LocationTrackingService extends Service {
    private static final String LOG_TAG = "LocationTrackingService";

    // 15 second update interval
    private static final long LOCATION_UPDATE_INTERVAL_MS = 5 * 1000;
    // We will take a location update every 5 seconds if it is available
    private static final long FASTEST_LOCATION_UPDATE_INTERVAL_MS = 1000;

    private static final int MIN_LOCATION_SPEED = 0;

    public static boolean IS_SERIVCE_RUNNING = false;


    //    private LocationListener listener;
    private FusedLocationProviderClient mLocationProvider;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private Location mLastLocation;
    private ArrayList<Location> mLocations;
    private ArrayList<LatLng> mLatLngs;
    private double mTotalDistance;

    @Override
    public void onCreate() {

        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);

        // init locations array
        mLocations = new ArrayList<>();
        mLatLngs = new ArrayList<>();

        // setup location request
        setupLocationRequest();

        // setup location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            handleStartAction();
        } else if(intent.getAction().equals(Constants.ACTION.EXTERNALSTOP_ACTION)) {
            handleExternalStopAction();
        } else if(intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            handleStopAction();
        }

        return START_STICKY;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Stopping Location Tracking Service");
        mLocationProvider.removeLocationUpdates(mLocationCallback);
//        mLatLngs.clear();
//        mLocations.clear();

    }

    private void handleStartAction() {
        Log.i(LOG_TAG, "Received Start Foreground Intent");
        // create notification intent
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Add Stop button intent to notification
        // TODO: need to fix crash from pressing stop when app is closed...look into activity state overrides
//        Intent stopIntent = new Intent(this, LocationTrackingService.class);
//        stopIntent.setAction(Constants.ACTION.EXTERNALSTOP_ACTION);
//        PendingIntent pStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);


        // Setup Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,Constants.NOTIFICATION_ID.FOREGROUND_CHANNEL_ID);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence ChannelName = "My Notification";
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATION_ID.FOREGROUND_CHANNEL_ID,"Drive Notification", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Driving");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }

        Notification notification = builder.setContentTitle("Drive In Progress")
                .setTicker("Driving")
                .setSmallIcon(R.drawable.ic_drive_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
//                .addAction(android.R.drawable.ic_menu_share,"Stop",pStopIntent)



        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        // start receiving updates
        boolean permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(permission) {
            Log.d(TAG, "onStartCommand: Registered location provider for location updates");
            mLocationProvider.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        } else {
            Log.d(TAG, "onStartCommand: Attempted to register location provider, but permission not granted");
        }
    }

    private void handleStopAction() {
        Log.i(LOG_TAG, "Received Stop Foreground Intent");
        shutdown();
    }

    private void handleExternalStopAction() {
        Log.i(LOG_TAG, "Received External Stop Foreground Intent");
        showActivity();
        shutdown();
    }

    private void showActivity() {
        Intent showHomeActivity = new Intent(this, HomeActivity.class);
        startActivity(showHomeActivity);
    }

    private void shutdown() {
        // send locations back to activity
        sendLocations();

        // only stops service running in foreground...will still run in background
        stopForeground(true);
        // stops service completely
        stopSelf();
    }

    private void sendLocations() {
        Log.d(TAG, "sendLocations: broadcasting locations");
        Intent intent = new Intent(Constants.ACTION.SENDLOCATIONS_ACTION);
        intent.putParcelableArrayListExtra("locations",mLatLngs);
        // NOTE: before adding distance, convert it to
        double distance_in_miles = mTotalDistance / 1609.34;
        intent.putExtra("distance",distance_in_miles);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        // cleanup

    }

    private void sendStoppedMessage() {
        Log.d(TAG, "sendStoppedMessage: broadcasting stop message");
        Intent intent = new Intent(Constants.ACTION.STOPMESSAGE_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        // check that location is valid and has a min speed
        if(lastLocation != null && lastLocation.getSpeed() >= MIN_LOCATION_SPEED) {
            if(lastLocation.hasAccuracy() && lastLocation.getAccuracy() <= 30) {
                // check if locations contains any elements, if so start to accumulate total distance
                if(!mLocations.isEmpty()) {
                    Location previousDistance = mLocations.get(mLocations.size() - 1);
                    mTotalDistance += lastLocation.distanceTo(previousDistance);
                    Log.d(TAG, "onNewLocation: Total distance is " + mTotalDistance);
                }
                mLocations.add(lastLocation);
                LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mLatLngs.add(latLng);
                Log.d(TAG, "onNewLocation: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
            } else {
                Log.w(TAG,"Location is not accurate enough to add to locations array: " + lastLocation.getAccuracy());
            }

        }
        else
            Log.w(TAG,"Location is not valid to add to locations array");
    }



}
