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

    private double mMpg;
    private double mPpg;
    private boolean mIncludeInsurance;
    private boolean mIncludeMaintenance;
    private double mInsurancePrice;
    private double mMaintenancePrice;


    private int numOfPassengers;
    public int getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(int numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
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

        mMpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_mpg_key),"0"));
        mPpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_ppg_key),"0"));
        mInsurancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));
        mMaintenancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_maintenance_key),"0"));
        mIncludeInsurance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_insurance_key),false);
        mIncludeMaintenance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_maintenance_key),false);

    }


    public void calculatePricePerRider() {
        double insurancePrice = mIncludeInsurance ? mInsurancePrice : 0;
        double maintenancePrice = mIncludeMaintenance ? mMaintenancePrice : 0;
        double price = RideCalculator.calculatePricePerRider(numOfPassengers,mMpg,mPpg,distance,insurancePrice,maintenancePrice,parkingCost);
        pricePerRider = price;

        // make formatted string verison of price
        String formattedPrice = String.format(Locale.US, "$%.2f",price);
        // update UI
        view.updatePriceTextView(formattedPrice);
    }

    public void addPassengerPressed() {
        if(numOfPassengers >= Constants.CONSTANTS.MAX_PASSENGERS) {
            view.disableAddPassengersButton();
            mAddButtonDisabled = true;
            return;
        }

        if(mRemoveButtonDisabled) {
            view.enableRemovePassengersButton();
            mRemoveButtonDisabled = false;
        }
        setNumOfPassengers(numOfPassengers + 1);
//        numOfPassengers++;
//        view.updateNumberOfPassengers(Integer.toString(numOfPassengers));
//
//        // calculate new price
//        calculatePricePerRider();
    }

    public void removePassengerPressed() {
        if(numOfPassengers <= Constants.CONSTANTS.MIN_PASSENGERS) {
            view.disableRemovePassengerButton();
            mRemoveButtonDisabled = true;
            return;
        }

        if(mAddButtonDisabled) {
            view.enableAddPassengersButton();
            mAddButtonDisabled = false;
        }
        setNumOfPassengers(numOfPassengers - 1);
//        numOfPassengers--;
//        view.updateNumberOfPassengers(Integer.toString(numOfPassengers));
//
//        // calculate new price
//        calculatePricePerRider();

    }
}
