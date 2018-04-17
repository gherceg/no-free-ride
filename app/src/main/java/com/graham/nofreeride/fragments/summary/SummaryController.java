package com.graham.nofreeride.fragments.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.graham.nofreeride.R;
import com.graham.nofreeride.utils.Constants;
import com.graham.nofreeride.utils.RideCalculator;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryController {

    SharedPreferences sharedPreferences;
    Context context;
    SummaryContract.view view;


    private double distance;
    public double getDistance() {
        return distance;
    }

    private double pricePerRider;

    public double getPricePerRider() {
        return pricePerRider;
    }


    // flags to include insurance and maintenance
    private boolean mIncludeInsurance;
    private boolean mIncludeMaintenance;

    public double getInsurancePrice() {
        return mInsurancePrice;
    }

    public double getMaintenancePrice() {
        return mMaintenancePrice;
    }

    // vars to hold shared preferences values
    // insurance price per year
    private double mInsurancePrice;
    // avg maintenance cost per mile
    private double mMaintenancePrice;
    // gas rate
    private double mMpg;
    // price for gas
    private double mPpg;


    // getter and setter for number of passengers
    private int numOfPassengers;
    public int getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(int numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
        checkNumberOfPassengers();
        view.updateNumberOfPassengers(Integer.toString(numOfPassengers));
        calculatePricePerRider();
    }

    private double parkingCost;
    public double getParkingCost() {
        return parkingCost;
    }

    public void setParkingCost(double parkingCost) {
        this.parkingCost = parkingCost;
        calculatePricePerRider();
    }



    private boolean mRemoveButtonDisabled = false;
    private boolean mAddButtonDisabled = false;

    public SummaryController(SummaryContract.view view, Context context, double mDistance, int numOfPassengers, double parkingCost) {
        this.view = view;
        this.context = context;

        // parameters for calculating drive costs
        this.distance = mDistance;
        this.numOfPassengers = numOfPassengers;
        this.parkingCost = parkingCost;

        // get shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        refreshSharedPreferences();


    }


    public void calculatePricePerRider() {
        double insurancePrice = mIncludeInsurance ? mInsurancePrice : 0;
        double maintenancePrice = mIncludeMaintenance ? mMaintenancePrice : 0;
        double pricePerRider = RideCalculator.calculatePricePerRider(numOfPassengers,mMpg,mPpg,distance,insurancePrice,maintenancePrice,parkingCost);

        // make formatted string verison of price
        String formattedPrice = String.format(Locale.US, "$%.2f",pricePerRider);
        // update UI
        view.updatePriceTextView(formattedPrice);
    }

    public void addPassengerPressed() {
        setNumOfPassengers(numOfPassengers + 1);

        if(mRemoveButtonDisabled) {
            view.enableRemovePassengersButton();
            mRemoveButtonDisabled = false;
        }
    }

    public void removePassengerPressed() {
        setNumOfPassengers(numOfPassengers - 1);

        if(mAddButtonDisabled) {
            view.enableAddPassengersButton();
            mAddButtonDisabled = false;
        }
    }

    private void checkNumberOfPassengers() {
        if(numOfPassengers >= Constants.CONSTANTS.MAX_PASSENGERS) {
            view.disableAddPassengersButton();
            mAddButtonDisabled = true;
        } else if(numOfPassengers <= Constants.CONSTANTS.MIN_PASSENGERS) {
            view.disableRemovePassengerButton();
            mRemoveButtonDisabled = true;
        }
    }

    public void refreshSharedPreferences() {
        mMpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_mpg_key),"0"));
        mPpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_ppg_key),"0"));
        mInsurancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));
        mMaintenancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_maintenance_key),"0"));
        mIncludeInsurance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_insurance_key),false);
        mIncludeMaintenance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_maintenance_key),false);
    }
}
