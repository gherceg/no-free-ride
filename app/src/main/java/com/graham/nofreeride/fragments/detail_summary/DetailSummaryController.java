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

    public double getMaintenancePrice() {
        return mMaintenancePrice;
    }

    public double getInsurancePrice() {
        return mInsurancePrice;
    }

    // maintenance
    private double mMaintenancePrice;

    public boolean isIncludeMaintenance() {
        return mIncludeMaintenance;
    }

    public boolean isIncludeInsurance() {
        return mIncludeInsurance;
    }

    private boolean mIncludeMaintenance;
    // insurance
    private double mInsurancePrice;
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
        checkNumberOfPassengers();
        mView.updateNumberOfPassengers(Integer.toString(numOfPassengers));
        calculatePricePerRider();
    }

    private double pricePerRider;

    public double getPricePerRider() {
        return pricePerRider;
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

    /**
     * Constructor for DetailSummaryController
     * @param context - context of activity that created fragment
     * @param view - view that created the controller, responsible for handling logic for
     * @param distance - distance
     */
    public DetailSummaryController(Context context, DetailSummaryContract.view view, double distance) {
        mContext = context;
        mView = view;
        this.distance = distance;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMPG = Double.parseDouble(sharedPreferences.getString(mContext.getString(R.string.pref_mpg_key),"0"));
        mPPG = Double.parseDouble(sharedPreferences.getString(mContext.getString(R.string.pref_ppg_key), "0"));
        mInsurancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));
        mMaintenancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_maintenance_key),"0"));
        mIncludeInsurance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_insurance_key),false);
        mIncludeMaintenance = sharedPreferences.getBoolean(context.getString(R.string.pref_include_maintenance_key),false);
    }

    /**
     * Method called from view to handle adding passengers
     */
    public void onAddPassengerButtonPressed() {
        setNumOfPassengers(numOfPassengers + 1);

        if(mRemoveButtonDisabled) {
            mView.enableRemovePassengerButton();
            mRemoveButtonDisabled = false;
        }
    }

    /**
     * Method called from view to handle removing passenger
     */
    public void onRemovePassengerButtonPressed() {
        setNumOfPassengers(numOfPassengers - 1);

        if(mAddButtonDisabled) {
            mView.enableAddPassengerButton();
            mAddButtonDisabled = false;
        }
    }

    /**
     * Method to check if current number of passengers equals min or max limit
     */
    private void checkNumberOfPassengers() {
        if(numOfPassengers >= Constants.CONSTANTS.MAX_PASSENGERS) {
            mView.disableAddPassengerButton();
            mAddButtonDisabled = true;
        } else if(numOfPassengers <= Constants.CONSTANTS.MIN_PASSENGERS) {
            mView.disableRemovePassengerButton();
            mRemoveButtonDisabled = true;
        }
    }

    /**
     * Method to handle update preferences when maintenance checkbox is changed
     * @param checked - boolean for whether checkbox is checked or not
     */
    public void onMaintenanceCheckBoxChanged(boolean checked) {
        // update class var
        mIncludeMaintenance = checked;
        // update shared preferences (TODO: leave default settings alone?)
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
        // update shared preferences (TODO: leave default settings alone?)
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
        double insurancePrice = mIncludeInsurance ? mInsurancePrice : 0;
        double maintenancePrice = mIncludeMaintenance ? mMaintenancePrice : 0;
        pricePerRider = RideCalculator.calculatePricePerRider(numOfPassengers,mMPG,mPPG,distance,insurancePrice,maintenancePrice,parkingCost);
        // format price
        String formattedPrice = String.format(Locale.US, "$%.2f",pricePerRider);
        // update UI
        mView.updatePricePerRiderText(formattedPrice);
    }
}
