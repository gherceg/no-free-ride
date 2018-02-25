package com.graham.nofreeride.riders;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.graham.nofreeride.Model.RideCalculator;
import com.graham.nofreeride.R;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class RidersController {

    RidersContract.view view;
    Context context;
    SharedPreferences sharedPreferences;

    private int mNumOfRiders;
    private double mDistance;
    private double mPricePerRider;


    public RidersController(RidersContract.view view, Context context, double mDistance) {
        this.view = view;
        this.context = context;
        this.mDistance = mDistance;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void onCalculateButtonPressed(int numOfRiders) {
        mNumOfRiders = numOfRiders;
        double mpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_mpg_key),"0"));
        double ppg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_ppg_key),"0"));
        double insurance = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));
        mPricePerRider = RideCalculator.calculatePricePerRider(numOfRiders,mpg,insurance,ppg,mDistance);
        view.showSummaryFragment(mPricePerRider);
    }


}
