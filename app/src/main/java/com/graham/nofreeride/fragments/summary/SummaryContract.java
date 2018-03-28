package com.graham.nofreeride.fragments.summary;

/**
 * Created by grahamherceg on 2/10/18.
 */

public interface SummaryContract {

    interface view {

        void updatePriceTextView(String price);
        void updateNumberOfPassengers(String passengers);

        void disableAddPassengersButton();
        void disableRemovePassengerButton();

        void enableRemovePassengersButton();

        void enableAddPassengersButton();
    }
}
