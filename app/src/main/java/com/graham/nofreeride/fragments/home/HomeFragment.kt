package com.graham.nofreeride.fragments.home

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

import com.graham.nofreeride.activities.HomeActivity
import com.graham.nofreeride.R
import com.graham.nofreeride.activities.SettingsActivity
import com.graham.nofreeride.utils.SharedPreferencesManager
import com.graham.nofreeride.utils.formatUSD

import java.util.Locale

/**
 * Created by grahamherceg on 2/2/18.
 * Fragment class for the home page, displaying driving settings
 */

class HomeFragment : Fragment(), View.OnClickListener, HomeViewInterface {

    private var listener: HomeFragmentListener? = null

    private var controller: HomeController? = null

    private var sharedPreferences: SharedPreferencesManager? = null

    private val notificationManager: NotificationManagerCompat?
    get() {
        context?.let {
            return NotificationManagerCompat.from(it)
        } ?: return null
    }

    // UI Elements
    private lateinit var insurancePriceTextView: TextView
    private lateinit var milesPerGallonTextView: TextView
    private lateinit var pricePerGallonTextView: TextView
    private lateinit var toggleDriveButton: Button

    // Metrics
    private var insurancePrice: Float = 0f
    private var milesPerGallon: Int = 0
    private var pricePerGallon: Float = 0f


    companion object {

        /**
         * Request Code for Location permissions
         */
        private val PERMISSIONS_REQUEST_LOCATION = 1
        private val uniqueChannelID = "M_CH_ID"
        private val uniqueNotificationID = 1
        private val notificationTitle = "Drive In Progress"
        private val notificationContent = "Distance: "

//        fun newInstance() : HomeFragment {
//
//        }
    }

    /**
     * Interface implemented by Home Activity to handle fragment transactions and service interactions
     */
    interface HomeFragmentListener {
        fun onStartDrivePressed()
        fun onStopDrivePressed()
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            listener = context as HomeFragmentListener?
        } catch (e: ClassCastException) {
            throw ClassCastException(context!!.toString() + " must implement HomeFragmentListener")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create controller
        controller = HomeController(this)

        //        if(savedInstanceState != null) {
        //            boolean driving = savedInstanceState.getBoolean("drive_in_progress", false);
        //            if(driving) {
        //                controller.onStartDrivingButtonPressed(insurancePrice,milesPerGallon,pricePerGallon);
        //            }
        //        }

        context?.let {
            sharedPreferences = SharedPreferencesManager(it)
            sharedPreferences?.let { preferences ->
                insurancePrice = preferences.getInsurancePrice()
                milesPerGallon = preferences.getMilesPerGallon()
                pricePerGallon = preferences.getPPG()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        // don't show back button in action bar on home page
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setHomeButtonEnabled(false)
        }


        sharedPreferences?.let { manager ->
            manager.getDriveInProgress()
        }

        val isDriveInProgress = sharedPreferences?.getDriveInProgress() ?: false
        controller?.driveInProgress = isDriveInProgress
        if (isDriveInProgress) {
            toggleDriveButton.setText(R.string.toggle_stop_drive_btn)
        }
    }

    override fun onPause() {
        super.onPause()
        storeDriveInProgress()
    }

    override fun onDestroy() {
        cleanup()
        super.onDestroy()
    }

    private fun cleanup() {

    }

    private fun storeDriveInProgress() {
        // Save whether a drive is in progress or not
        sharedPreferences?.saveDriveInProgress(controller?.driveInProgress ?: false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        insurancePriceTextView = view.findViewById<View>(R.id.insurance_value) as TextView
        insurancePriceTextView.setOnClickListener(this)
        milesPerGallonTextView = view.findViewById<View>(R.id.tv_miles_per_gallon) as TextView
        milesPerGallonTextView.setOnClickListener(this)
        pricePerGallonTextView = view.findViewById<View>(R.id.tv_price_per_gallon) as TextView
        pricePerGallonTextView.setOnClickListener(this)
        toggleDriveButton = view.findViewById<View>(R.id.btn_toggle_drive) as Button
        toggleDriveButton.setOnClickListener(this)


        insurancePriceTextView.text = formatUSD(insurancePrice)
        milesPerGallonTextView.text = milesPerGallon.toString()
        pricePerGallonTextView.text = formatUSD(pricePerGallon)


        // request permission to use location
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION)

        return view
    }

    //    @Override
    //    public void onSaveInstanceState(Bundle outState) {
    //        outState.putBoolean("drive_in_progress",controller.isDriving());
    //    }

    fun driveStoppedExternally() {
        // set drive in progress to false
        controller?.driveInProgress = false
        storeDriveInProgress()
        // update UI
        toggleDriveButton.text = resources.getString(R.string.prepare_drive_btn_text)
    }

    fun updateDriveStatus(driveInProgress: Boolean) {
        controller?.driveInProgress = driveInProgress
        if (driveInProgress) {
            toggleDriveButton.setText(R.string.toggle_stop_drive_btn)
        } else {
            toggleDriveButton.text = resources.getString(R.string.prepare_drive_btn_text)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_toggle_drive -> controller?.onDriveButtonPressed()
            R.id.insurance_value, R.id.tv_miles_per_gallon, R.id.tv_price_per_gallon -> {
                val startSettingsActivity = Intent(activity, SettingsActivity::class.java)
                startActivity(startSettingsActivity)
            }
        }
    }


    /**
     * Callback from Controller that
     */
    override fun startDrive() {
        // Start background location tracking service
        listener?.onStartDrivePressed()

        // update UI
        toggleDriveButton.text = getString(R.string.end_drive)
    }

    override fun stopDrive() {
        // tell activity to stop the drive
        listener?.onStopDrivePressed()

        // update UI
        toggleDriveButton.text = resources.getString(R.string.prepare_drive_btn_text)
    }



    // Create notification
    private fun showDrivingNotification(distance: Double) {
        // Build notification
        val notificationBuilder = NotificationCompat.Builder(context!!, uniqueChannelID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker(getString(R.string.driving))
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_drive_notification)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent + String.format(Locale.US, "%.2f miles", distance))
                .setWhen(System.currentTimeMillis())
                .setColor(resources.getColor(R.color.colorPrimary, null))
                .setOngoing(true)

        // create intent to send back to home screen if notification is clicked
        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        notificationManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(uniqueChannelID, "Drive Notification", NotificationManager.IMPORTANCE_HIGH)

                // Configure the notification channel.
                notificationChannel.description = "Driving"
                notificationChannel.enableLights(true)
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                notificationChannel.enableVibration(false)

                it.createNotificationChannel(notificationChannel)

            }
            it.notify(uniqueNotificationID, notificationBuilder.build())
        }

    }


    private fun removeDrivingNotification() {
        // remove notification
        notificationManager?.let {
            it.cancel(uniqueNotificationID)
        }
    }

    private fun requestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 24 && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                toggleDriveButton.isEnabled = true
            } else {
                // permission denied
                Toast.makeText(context, "Cannot track your drive without access to your location", Toast.LENGTH_LONG).show()
                requestPermissions()
            }
        }
    }

}
