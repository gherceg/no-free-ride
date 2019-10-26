package com.graham.nofreeride.fragments.home


/**
 * Created by grahamherceg on 2/2/18.
 */

class HomeController(val viewInterface: HomeViewInterface) {

/**
 * Constructor for the HomeController class
 * @param viewInterface - Interface to communicate with the fragment
 */


    var driveInProgress = false

    /**
     * This method handles logic for when start drive button is pressed on the view
     */
    fun onDriveButtonPressed() {
        if (driveInProgress) {
            stopDrive()
        } else {
            startDrive()
        }
    }

    /**
     * This method handles what to do when starting a drive
     */
    private fun startDrive() {
        // update drive flag
        driveInProgress = true
        // tell view that the drive should be started (which will tell the activity)
        // TODO: could this be improved???
        viewInterface.startDrive()
    }

    /**
     * This method handles what to do when stopping a drive
     */
    private fun stopDrive() {
        // update drive flag
        driveInProgress = false
        // uses a service
        viewInterface.stopDrive()
    }

}
