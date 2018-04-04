package com.graham.nofreeride.fragments.detail_summary;

/**
 * Created by grahamherceg on 3/29/18.
 */

public interface DetailSummaryContract {
    interface view {

        void updateNumberOfPassengers(String passengers);

        void updatePricePerRiderText(String price);

        void updateParkingCostEditText(double cost);
    }
}
