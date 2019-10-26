package com.graham.nofreeride.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.google.android.gms.maps.model.LatLng
import com.graham.nofreeride.R
import com.graham.nofreeride.fragments.detail_summary.DetailSummaryFragment
import com.graham.nofreeride.fragments.home.HomeFragment
import com.graham.nofreeride.fragments.summary.SummaryFragment
import com.graham.nofreeride.utils.Constants
import com.graham.nofreeride.services.LocationTrackingService

import java.util.ArrayList

/**
 * Home Activity class responsible for managing fragments, starting/stopping service, and receiving broadcasts from services
 */

class HomeActivity : AppCompatActivity(), HomeFragment.HomeFragmentListener, SummaryFragment.SummaryFragmentListener, DetailSummaryFragment.DetailSummaryFragmentListener {

    private val HOME_FRAGMENT_TAG = "home_fragment"
    private val SUMMARY_FRAGMENT_TAG = "summary_fragment"
    private val DETAIL_SUMMARY_FRAGMENT_TAG = "detailed_summary_fragment"

    // private members to pass data back and fourth from fragments
    // Getters for fragments
    var parkingCost: Double = 0.0
    var numOfPassengers: Int = 0


    private val mLocationReceiver = object : BroadcastReceiver() {
        val TAG = "Broadcast Receiver"


        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Constants.ACTION.SENDLOCATIONS_ACTION) {
                //                Log.d(TAG, "onReceive: locations were received by activity");
                val latLngs = intent.getParcelableArrayListExtra<LatLng>("locations")
                val distance = intent.getDoubleExtra("distance", 0.0)
                // if locations are empty don't move to the next page
                if (latLngs!!.isEmpty()) {
                    //                    Log.d(TAG, "onReceive: no locations exist yet..not showing summary page");
                    Toast.makeText(applicationContext, "Not enough data", Toast.LENGTH_SHORT).show()
                } else {
                    showSummaryPage(latLngs, distance)
                }
            } else if (intent.action == Constants.ACTION.STOPMESSAGE_ACTION) {
                //                Log.d(TAG, "onReceive: received a stop message");

                // tell the home fragment to update accordingly
                val fragment = supportFragmentManager.findFragmentByTag(HOME_FRAGMENT_TAG)
                if (fragment != null && fragment is HomeFragment) {
                    fragment.driveStoppedExternally()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // set defaults
        parkingCost = 0.0
        numOfPassengers = 1

        // set up toolbar
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setLogo(R.mipmap.ic_app_icon)
            supportActionBar!!.setTitle("")
        }

        // create and set the home fragment
        val homeFragment = HomeFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.frag_container, homeFragment, HOME_FRAGMENT_TAG)
                .commit()


        // register to receive intents named "custom-event-name"
        val broadcastManager = LocalBroadcastManager.getInstance(this)
        broadcastManager.registerReceiver(mLocationReceiver, IntentFilter(Constants.ACTION.SENDLOCATIONS_ACTION))
        broadcastManager.registerReceiver(mLocationReceiver, IntentFilter(Constants.ACTION.STOPMESSAGE_ACTION))
    }

    /**
     * This method is called when activity is being destroyed.
     * Important to unregister the receiver
     */
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    /**
     * This method handles any click events on option items.
     * Only used for the menu and back button right now
     * @param item - contains information about which item was selected
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val startSettingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(startSettingsActivity)
                return true
            }
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // TODO: do I need both this method and onOptionsItemSelected
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * This method is called when the fragment UI receives a start drive event.
     * Responsible for starting the foreground service to track user's location.
     */
    override fun onStartDrivePressed() {
        // start location tracking service
        val startIntent = Intent(this@HomeActivity, LocationTrackingService::class.java)
        startIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION
        startService(startIntent)
    }

    /**
     * This method is called when the fragment UI receives a stop drive event.
     * Responsible for stopping the foreground service completely
     */
    override fun onStopDrivePressed() {
        val stopIntent = Intent(this@HomeActivity, LocationTrackingService::class.java)
        stopIntent.action = Constants.ACTION.STOPFOREGROUND_ACTION
        startService(stopIntent)
    }

    /**
     * This method is called when the activity receives a broadcast message containing data from the service.
     * @param latLngs - array of latitude, longitude pairs in the order they were collected
     * @param distance - total distance traveled
     */
    private fun showSummaryPage(latLngs: ArrayList<LatLng>?, distance: Double) {
        Log.d(LOG_TAG, "showSummaryPage: attempting to show summary fragment")
        val summaryFragment = SummaryFragment.newInstance(distance, latLngs)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frag_container, summaryFragment, SUMMARY_FRAGMENT_TAG).addToBackStack(null).commit()
    }

    // ------ Callbacks from Summary Fragment ------

    /**
     * Method to handle adding Detail Summary Fragment
     * @param distance - pass the distance to the detail summary fragment
     * @param passengers - pass the number of passengers as user may have changed it before navigating to detail page
     */
    override fun onSummarySwipeUp(distance: Double, passengers: Int) {
        // set number of passengers on activity
        numOfPassengers = passengers
        // start detailed summary page
        val detailSummaryFragment = DetailSummaryFragment.newInstance(distance, passengers)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frag_container, detailSummaryFragment, DETAIL_SUMMARY_FRAGMENT_TAG).addToBackStack(null).commit()
    }

    // ------ Callbacks from Detail Summary Fragment ------
    /**
     * Method to handle saving parking cost added by user
     * @param parking - cost of parking added by user
     */
    override fun onParkingCostUpdated(parking: Double) {
        parkingCost = parking
    }

    /**
     * Method called when detail summary page is swiped down
     */
    override fun onDetailSummarySwipeDown(passengers: Int) {
        // set number of passengers on Activity
        numOfPassengers = passengers

        //        Log.d("Home_Activity", "onDetailSummarySwipeDown: trying to pop the stack");
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()

    }

    companion object {
        val LOG_TAG = "Home Activity"
    }
}
