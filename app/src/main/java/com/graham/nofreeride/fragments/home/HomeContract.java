package com.graham.nofreeride.fragments.home;

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

        void displaySummaryPage(double distance);

        void updateDriveNotifcation(double distance);
    }

    interface controller {
    }
}
