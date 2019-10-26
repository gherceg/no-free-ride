package com.graham.nofreeride.fragments.home;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.graham.nofreeride.activities.HomeActivity;
import com.graham.nofreeride.R;
import com.graham.nofreeride.activities.SettingsActivity;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/2/18.
 * Fragment class for the home page, displaying driving settings
 */

public class HomeFragment extends Fragment implements HomeContract.view, View.OnClickListener {


    /**
     * Interface implemented by Home Activity to handle fragment transactions and service interactions
     */
    public interface HomeFragmentListener {
        void onStartDrivePressed();
        void onStopDrivePressed();
    }

    /**
     * Request Code for Location permissions
     */
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;


    View view;

    HomeFragmentListener mListener;

    HomeController controller;
    SharedPreferences sharedPreferences;

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
    Button toggleDriveButton;

    // Metrics
    String mInsurancePrice;
    String mMPG;
    String mPPG;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create controller
        controller = new HomeController(getContext(),this);

//        if(savedInstanceState != null) {
//            boolean driving = savedInstanceState.getBoolean("drive_in_progress", false);
//            if(driving) {
//                controller.onStartDrivingButtonPressed(mInsurancePrice,mMPG,mPPG);
//            }
//        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mInsurancePrice = sharedPreferences.getString(getString(R.string.pref_insurance_price_key),"-");
        mMPG = sharedPreferences.getString(getString(R.string.pref_mpg_key),"-");
        mPPG = sharedPreferences.getString(getString(R.string.pref_ppg_key),"-");
    }

    @Override
    public void onResume() {
        super.onResume();
        // don't show back button in action bar on home page
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        boolean isDriveInProgress = sharedPreferences.getBoolean("drive_in_progress",false);
        controller.driveInProgress = isDriveInProgress;
        if(isDriveInProgress) {
            toggleDriveButton.setText(R.string.toggle_stop_drive_btn);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        storeDriveInProgress();
    }

    private void storeDriveInProgress() {
        // Save whether a drive is in progress or not
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("drive_in_progress",controller.driveInProgress);
        // want to save preference immediately right? so use commit and not apply
        editor.commit();
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
            toggleDriveButton = (Button)view.findViewById(R.id.btn_toggle_drive);
            toggleDriveButton.setOnClickListener(this);
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

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putBoolean("drive_in_progress",controller.isDriving());
//    }

    public void driveStoppedExternally() {
        // set drive in progress to false
        controller.driveInProgress = false;
        storeDriveInProgress();
        // update UI
        toggleDriveButton.setText(getResources().getString(R.string.prepare_drive_btn_text));
    }

    public void updateDriveStatus(boolean driveInProgress) {
        controller.driveInProgress = driveInProgress;
        if(driveInProgress) {
            toggleDriveButton.setText(R.string.toggle_stop_drive_btn);
        } else {
            toggleDriveButton.setText(getResources().getString(R.string.prepare_drive_btn_text));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_toggle_drive:
                controller.onDriveButtonPressed(
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


    /**
     * Callback from Controller that
     */
    @Override
    public void startDrive() {
        // Start background location tracking service
        mListener.onStartDrivePressed();

        // update UI
        toggleDriveButton.setText("End Drive");
    }

    @Override
    public void stopDrive() {
        // tell activity to stop the drive
        mListener.onStopDrivePressed();

        // update UI
        toggleDriveButton.setText(getResources().getString(R.string.prepare_drive_btn_text));
    }



//    public void showPermissionRequiredToast() {
//        Toast.makeText(getContext(), "Cannot track location with no location",Toast.LENGTH_LONG).show();
//    }


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
                    toggleDriveButton.setEnabled(true);
                } else {
                    // permission denied
                    Toast.makeText(getContext(), "Cannot track your drive without access to your location", Toast.LENGTH_LONG).show();
                    requestPermissions();
                }
        }
    }

}
