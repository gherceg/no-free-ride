package com.graham.nofreeride.fragments.detail_summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.graham.nofreeride.R;
import com.graham.nofreeride.utils.Constants;
import com.graham.nofreeride.utils.RideCalculator;

import java.util.Locale;

/**
 * Created by grahamherceg on 3/29/18.
 */

public class DetailSummaryController {

    SharedPreferences sharedPreferences;
    Context mContext;
    DetailSummaryContract.view mView;


    // miles per gallon
    private double mMPG;
    // price per gallon
    private double mPPG;
    // maintenance
    private double mMaintenanceCosts;
    private boolean mIncludeMaintenance;
    // insurance
    private double mInsuranceCosts;
    private boolean mIncludeInsurance;

    private boolean mAddButtonDisabled;
    private boolean mRemoveButtonDisabled;

    private double distance;

    public double getDistance() {
        return distance;
    }

    private int numOfPassengers;
    public int getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(int numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
        mView.updateNumberOfPassengers(Integer.toString(numOfPassengers));
        calculatePricePerRider();
    }

    private double parkingCost;
    public double getParkingCost() {
        return parkingCost;
    }

    public void setParkingCost(double parkingCost) {
        this.parkingCost = parkingCost;
        mView.updateParkingCostEditText(parkingCost);
        calculatePricePerRider();
    }

    public DetailSummaryController(Context context, DetailSummaryContract.view view, double distance, int passengers) {
        mContext = context;
        mView = view;
        this.distance = distance;
        numOfPassengers = passengers;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMPG = Double.parseDouble(sharedPreferences.getString(mContext.getString(R.string.pref_mpg_key),"0"));

    }


    public void onAddPassengerButtonPressed() {
        if(numOfPassengers >= Constants.CONSTANTS.MAX_PASSENGERS) {
//            mView.disableAddPassengersButton();
            mAddButtonDisabled = true;
            return;
        }

        if(mRemoveButtonDisabled) {
//            mView.enableRemovePassengersButton();
            mRemoveButtonDisabled = false;
        }
        numOfPassengers++;
        mView.updateNumberOfPassengers(Integer.toString(numOfPassengers));

        // calculate new price per rider
        calculatePricePerRider();

    }

    public void onRemovePassengerButtonPressed() {
        if(numOfPassengers <= Constants.CONSTANTS.MIN_PASSENGERS) {
            mRemoveButtonDisabled = true;
            return;
        }

        if(mAddButtonDisabled) {
            mAddButtonDisabled = false;
        }
        numOfPassengers--;
        mView.updateNumberOfPassengers(Integer.toString(numOfPassengers));

        // calculate new price per rider
        calculatePricePerRider();

    }

    /**
     * Method to handle update preferences when maintenance checkbox is changed
     * @param checked - boolean for whether checkbox is checked or not
     */
    public void onMaintenanceCheckBoxChanged(boolean checked) {
        // update class var
        mIncludeMaintenance = checked;
        // update shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_include_maintenance_key),checked);
        editor.apply();
        // update price per rider
        calculatePricePerRider();
    }

    /**
     * Method to handle update preferences when insurance checkbox is changed
     * @param checked - boolean for whether checkbox is checked or not
     */
    public void onInsuranceCheckBoxChanged(boolean checked) {
        // update class var
        mIncludeInsurance = checked;
        // update shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mContext.getString(R.string.pref_include_insurance_key),checked);
        editor.apply();
        // update price per rider
        calculatePricePerRider();
    }

    /**
     * Method to calculate price per rider and update the view
     */
    public void calculatePricePerRider() {
        double insurancePrice = mIncludeInsurance ? mInsuranceCosts : 0;
        double maintenancePrice = mIncludeMaintenance ? mMaintenanceCosts : 0;
        double price = RideCalculator.calculatePricePerRider(numOfPassengers,mMPG,mPPG,distance,insurancePrice,maintenancePrice,parkingCost);
        String formattedPrice = String.format(Locale.US, "$%.2f",price);
        // update UI
        mView.updatePricePerRiderText(formattedPrice);
    }
}
