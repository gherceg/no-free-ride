package com.graham.nofreeride.home_page;

/**
 * Created by grahamherceg on 2/2/18.
 */

public interface HomeContract {

    interface view {


        void showInvalidInputsToast();

        void driveHasStarted();
        void startDrive();
        void driveHasEnded();
        void endDrive();

        void showPermissionRequiredToast();

        void displayDistance(double distance);

        void updateDriveNotifcation(double distance);
    }

    interface controller {
    }
}
