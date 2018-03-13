package com.graham.nofreeride.home_page;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.graham.nofreeride.activities.HomeActivity;
import com.graham.nofreeride.R;
import com.graham.nofreeride.activities.SettingsActivity;
import com.graham.nofreeride.riders.RidersFragment;
import com.graham.nofreeride.utils.LocationTrackingService;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/2/18.
 */

public class HomeFragment extends Fragment implements HomeContract.view, View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    View view;


    HomeController controller;
    SharedPreferences sharedPreferences;
    FusedLocationProviderClient locationProviderClient;

    NotificationCompat.Builder mNotificationBuilder;
    NotificationManager mNotificationManager;
    private static final String uniqueChannelID = "M_CH_ID";
    private static final int uniqueNotificationID = 1;
    private static final String notificationTitle = "Drive In Progress";
    private static final String notificationContent = "Distance: ";

    // UI Elements
    TextView insurancePriceTextView;
    TextView milesPerGallonTextView;
    TextView pricePerGallonTextView;
    Button startDrivingButton;

    // Metrics
    String mInsurancePrice;
    String mMPG;
    String mPPG;

    Boolean mDriveInProgress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create controller
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        controller = new HomeController(getContext(),this,locationProviderClient);

        if(savedInstanceState != null) {
            boolean driving = savedInstanceState.getBoolean("drive_in_progress", false);
            if(driving) {
                controller.onStartDrivingButtonPressed(mInsurancePrice,mMPG,mPPG);
            }
        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mInsurancePrice = sharedPreferences.getString(getString(R.string.pref_insurance_price_key),"-");
        mMPG = sharedPreferences.getString(getString(R.string.pref_mpg_key),"-");
        mPPG = sharedPreferences.getString(getString(R.string.pref_ppg_key),"-");
        mDriveInProgress = false;

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mDriveInProgress) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            insurancePriceTextView = (TextView)view.findViewById(R.id.tv_insurance_per_month);
            insurancePriceTextView.setOnClickListener(this);
            milesPerGallonTextView = (TextView)view.findViewById(R.id.tv_miles_per_gallon);
            milesPerGallonTextView.setOnClickListener(this);
            pricePerGallonTextView = (TextView)view.findViewById(R.id.tv_price_per_gallon);
            pricePerGallonTextView.setOnClickListener(this);
            startDrivingButton = (Button)view.findViewById(R.id.btn_prepare_drive);
            startDrivingButton.setOnClickListener(this);
        }



        String insurance = mInsurancePrice.equals("-") ? mInsurancePrice :"$" + mInsurancePrice;
        String ppg = mPPG.equals("-") ? mPPG : "$" + mPPG;
        insurancePriceTextView.setText(insurance);
        milesPerGallonTextView.setText(mMPG);
        pricePerGallonTextView.setText(ppg);


        // request permission to use location
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_LOCATION);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("drive_in_progress",controller.isDriving());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_prepare_drive:
                controller.onStartDrivingButtonPressed(
                        mInsurancePrice,
                        mMPG,
                        mPPG);
                break;
            case R.id.tv_insurance_per_month:
            case R.id.tv_miles_per_gallon:
            case R.id.tv_price_per_gallon:
                Intent startSettingsActivity = new Intent(getActivity(), SettingsActivity.class);
                startActivity(startSettingsActivity);
                break;
        }
    }

    @Override
    public void showInvalidInputsToast() {
        Toast.makeText(getContext(), "Change metrics in settings", Toast.LENGTH_LONG).show();
    }



    @Override
    public void driveHasStarted() {
        // set button text
        startDrivingButton.setText("End Drive");
        showDrivingNotification(0);
    }

    @Override
    public void driveHasEnded() {
        // update any UI elements
        startDrivingButton.setText(getResources().getString(R.string.prepare_drive_btn_text));

        // get rid of the driving notification
        removeDrivingNotification();
    }


    // NOTE: For use if I ever get a service working correctly
    // ------------------------------
    @Override
    public void startDrive() {
        // Start background location tracking service
        Intent i = new Intent(getContext().getApplicationContext(), LocationTrackingService.class);
        getContext().startService(i);
    }

    @Override
    public void endDrive() {
        // Stop background location tracking service
        Intent i = new Intent(getContext().getApplicationContext(), LocationTrackingService.class);
        getContext().stopService(i);
    }

    // ----------------------------

    @Override
    public void showPermissionRequiredToast() {
        Toast.makeText(getContext(), "Cannot track location with no location",Toast.LENGTH_LONG).show();
    }

    // TODO: RENAME THIS
    @Override
    public void displayDistance(double distance) {
        // start the select num of riders fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.frag_container,RidersFragment.newInstance(distance)).addToBackStack(null).commit();
        fragmentManager.beginTransaction().replace(R.id.riders_frag_container,RidersFragment.newInstance(distance)).commit();

        // disable drive button
        startDrivingButton.setEnabled(false);

//        String distanceFormatted = String.format("%.2f m",distance);
    }

    @Override
    public void updateDriveNotifcation(double distance) {
        showDrivingNotification(distance);
    }


    // Create notification
    private void showDrivingNotification(double distance) {
        // Build notification
        mNotificationBuilder = new NotificationCompat.Builder(getContext(), uniqueChannelID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Driving")
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_drive_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent + String.format(Locale.US, "%.2f miles",distance))
                .setWhen(System.currentTimeMillis())
                .setColor(getResources().getColor(R.color.colorPrimary,null))
                .setOngoing(true);

        // create intent to send back to home screen if notification is clicked
        Intent intent = new Intent(getContext(), HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(pendingIntent);

        // Get instance of notification manager class
        mNotificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(uniqueChannelID,"Drive Notification", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Driving");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);

            // submit to notification manager
            if(mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        mNotificationManager.notify(uniqueNotificationID, mNotificationBuilder.build());
    }


    private void removeDrivingNotification() {
        // remove notification
        mNotificationManager.cancel(uniqueNotificationID);
    }

    private boolean requestPermissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    startDrivingButton.setEnabled(true);
                } else {
                    // permission denied
                    Toast.makeText(getContext(), "Cannot track your drive without access to your location", Toast.LENGTH_LONG).show();
                    requestPermissions();
                }
        }
    }

}
