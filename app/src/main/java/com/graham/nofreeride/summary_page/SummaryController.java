package com.graham.nofreeride.summary_page;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.graham.nofreeride.R;
import com.graham.nofreeride.riders.RidersContract;
import com.graham.nofreeride.utils.RideCalculator;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryController {
    SharedPreferences sharedPreferences;
    Context context;
    SummaryContract.view view;

    private double mDistance;

    private double mMpg;
    private double mPpg;
    private double mInsurancePrice;


    public SummaryController(SummaryContract.view view, Context context, double mDistance) {
        this.view = view;
        this.context = context;
        this.mDistance = mDistance;

        // get shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mMpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_mpg_key),"0"));
        mPpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_ppg_key),"0"));
        mInsurancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));

    }

    public void calculatePricePerRider(int numOfRiders) {
        double price = RideCalculator.calculatePricePerRider(numOfRiders,mMpg,mPpg,mDistance);
        String formattedPrice = String.format(Locale.US, "$%.2f",price);

        // update UI
        view.updatePriceTextView(formattedPrice);
    }

}
