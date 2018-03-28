package com.graham.nofreeride.fragments.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.graham.nofreeride.R;
import com.graham.nofreeride.utils.RideCalculator;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryController {

    private static int MIN_PASSENGERS = 0;
    private static int MAX_PASSENGERS = 5;

    SharedPreferences sharedPreferences;
    Context context;
    SummaryContract.view view;

    private double mDistance;

    private double mMpg;
    private double mPpg;
    private double mInsurancePrice;

    public int numOfPassengers;

    private boolean mRemoveButtonDisabled = false;
    private boolean mAddButtonDisabled = false;

    public SummaryController(SummaryContract.view view, Context context, double mDistance) {
        this.view = view;
        this.context = context;
        this.mDistance = mDistance;

        // get shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        mMpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_mpg_key),"0"));
        mPpg = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_ppg_key),"0"));
        mInsurancePrice = Double.parseDouble(sharedPreferences.getString(context.getString(R.string.pref_insurance_price_key),"0"));

        // default # of passengers to 1
        numOfPassengers = 1;
    }


    public void calculatePricePerRider(int numOfRiders) {
        double price = RideCalculator.calculatePricePerRider(numOfRiders,mMpg,mPpg,mDistance);
        String formattedPrice = String.format(Locale.US, "$%.2f",price);

        // update UI
        view.updatePriceTextView(formattedPrice);
    }

    public void addPassengerPressed() {
        if(numOfPassengers >= MAX_PASSENGERS) {
            view.disableAddPassengersButton();
            mAddButtonDisabled = true;
            return;
        }

        if(mRemoveButtonDisabled) {
            view.enableRemovePassengersButton();
            mRemoveButtonDisabled = false;
        }
        numOfPassengers++;
        calculatePricePerRider(numOfPassengers);
        view.updateNumberOfPassengers(Integer.toString(numOfPassengers));
    }

    public void removePassengerPressed() {
        if(numOfPassengers <= MIN_PASSENGERS) {
            view.disableRemovePassengerButton();
            mRemoveButtonDisabled = true;
            return;
        }

        if(mAddButtonDisabled) {
            view.enableAddPassengersButton();
            mAddButtonDisabled = false;
        }

        numOfPassengers--;
        calculatePricePerRider(numOfPassengers);
        view.updateNumberOfPassengers(Integer.toString(numOfPassengers));
    }
}
