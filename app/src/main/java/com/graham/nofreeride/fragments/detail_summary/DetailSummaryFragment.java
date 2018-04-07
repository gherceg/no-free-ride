package com.graham.nofreeride.fragments.detail_summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.graham.nofreeride.R;
import com.graham.nofreeride.activities.HomeActivity;
import com.graham.nofreeride.utils.Constants;
import com.graham.nofreeride.utils.EditTextEnterEvent;
import com.graham.nofreeride.utils.EditTextImeBackListener;

import java.util.Locale;

/**
 * Created by grahamherceg on 3/26/18.
 */

public class DetailSummaryFragment extends Fragment implements View.OnClickListener, DetailSummaryContract.view, EditTextImeBackListener {


    public interface DetailSummaryFragmentListener {
        void onDetailSummarySwipeDown(int passengers);
        void onParkingCostUpdated(double parking);
    }

    SharedPreferences sharedPreferences;
    DetailSummaryController controller;

    // UI View Connections
    View view;
    ImageButton removePassengerButton;
    ImageButton addPassengerButton;
    ImageButton hideDetailSummaryButton;

    TextView numOfPassengersTextView;
    TextView pricePerRiderTextView;

    CheckBox maintenanceCheckBox;
    CheckBox insuranceCheckBox;
    EditTextEnterEvent addParkingEditText;


    private double mPricePerRider;
    private double mDistance;
    private int mNumOfPassengers;

    private boolean mIncludeMaintenance;
    private boolean mIncludeInsurance;
    private String mMaintenanceCost;
    private String mInsuranceCost;

    private GestureDetector mDetector;
    private double scrollDistanceY = 0;

    private DetailSummaryFragmentListener mListener;


    /**
     * Static initializer for DetailSummaryFragment
     *
     * @return fragment - instance of the newly created DetailSummaryFragment
     */
    public static DetailSummaryFragment newInstance(double distance, int passengers) {
        DetailSummaryFragment fragment = new DetailSummaryFragment();
        Bundle args = new Bundle();
        args.putInt("passengers", passengers);
        args.putDouble("distance", distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DetailSummaryFragment.DetailSummaryFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SummaryFragmentListener");
        }
        // get shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        double parkingCost = ((HomeActivity)getActivity()).getParkingCost();
        controller.setParkingCost(parkingCost);
        int passengers = ((HomeActivity)getActivity()).getNumOfPassengers();
        // handle logic for passengers at min or max limits
        controller.setNumOfPassengers(passengers);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        int passengers = args.getInt("passengers");
        double distance = args.getDouble("distance");

        // extract preferences
        mIncludeInsurance = sharedPreferences.getBoolean(getString(R.string.pref_include_insurance_key), false);
        mInsuranceCost = sharedPreferences.getString(getString(R.string.pref_insurance_price_key), "0.00");
//         = Boolean.parseBoolean(includeInsurance);
        mIncludeMaintenance = sharedPreferences.getBoolean(getString(R.string.pref_include_maintenance_key), false);
        mMaintenanceCost = sharedPreferences.getString(getString(R.string.pref_maintenance_key), "0.00");

        controller = new DetailSummaryController(getContext(), this, distance, passengers);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_detail_summary, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(4);
        }


        // not using for now
//        mDetector = new GestureDetector(getContext(), new GestureListener());
//        view.setOnTouchListener(this);

        // setup buttons
        removePassengerButton = (ImageButton) view.findViewById(R.id.btn_detail_remove_rider);
        removePassengerButton.setOnClickListener(this);

        addPassengerButton = (ImageButton) view.findViewById(R.id.btn_detail_add_rider);
        addPassengerButton.setOnClickListener(this);

        hideDetailSummaryButton = (ImageButton) view.findViewById(R.id.btn_hide_summary);
        hideDetailSummaryButton.setOnClickListener(this);

        numOfPassengersTextView = (TextView) view.findViewById(R.id.tv_detail_num_of_passengers);
        numOfPassengersTextView.setText(String.format(Locale.US, "%d", mNumOfPassengers));

        pricePerRiderTextView = (TextView) view.findViewById(R.id.tv_detail_price_per_rider);
        pricePerRiderTextView.setText(String.format(Locale.US, "%.2f", mPricePerRider));

        insuranceCheckBox = (CheckBox) view.findViewById(R.id.cb_insurance);
        // need to parse insurance price from monthly price and miles driven
        double insurancePerMonth = Double.parseDouble(mInsuranceCost);
        double insurancePercent = insurancePerMonth * Constants.CONSTANTS.PCT_INSURANCE * controller.getDistance();
        String insurance = String.format(Locale.US, "Insurance ($%.2f)", insurancePercent);
        insuranceCheckBox.setText(insurance);
        insuranceCheckBox.setOnClickListener(this);
        insuranceCheckBox.setChecked(mIncludeInsurance);

        maintenanceCheckBox = (CheckBox) view.findViewById(R.id.cb_maintenance);
        double maintenceCost = Double.parseDouble(mMaintenanceCost) * controller.getDistance();
        String maintenanceText = String.format(Locale.US, "Maintenance ($%.2f)", maintenceCost);
        maintenanceCheckBox.setText(maintenanceText);
        maintenanceCheckBox.setOnClickListener(this);
        maintenanceCheckBox.setChecked(mIncludeMaintenance);

        addParkingEditText = (EditTextEnterEvent) view.findViewById(R.id.et_add_parking);
        addParkingEditText.setOnEditTextImeBackListener(this);

        // update price per rider
        controller.calculatePricePerRider();

        return view;
    }


    // Controller callback methods

    /**
     * @param passengers
     */
    @Override
    public void updateNumberOfPassengers(String passengers) {
        numOfPassengersTextView.setText(passengers);
    }

    @Override
    public void updatePricePerRiderText(String price) {
        pricePerRiderTextView.setText(price);
    }

    @Override
    public void updateParkingCostEditText(double cost) {
        if(cost != 0) {
            String formatted = String.format(Locale.US,"%.2f",cost);
            addParkingEditText.setText(formatted);
        } else {
            // show the hint again?
            addParkingEditText.setText(null);
        }
    }

    @Override
    public void enableAddPassengerButton() {
        addPassengerButton.setEnabled(true);
        addPassengerButton.setColorFilter(getResources().getColor(R.color.white,null));
    }

    @Override
    public void disableAddPassengerButton() {
        addPassengerButton.setEnabled(false);
        addPassengerButton.setColorFilter(getResources().getColor(R.color.grey,null));
    }

    @Override
    public void enableRemovePassengerButton() {
        removePassengerButton.setEnabled(true);
        removePassengerButton.setColorFilter(getResources().getColor(R.color.white,null));
    }

    @Override
    public void disableRemovePassengerButton() {
        removePassengerButton.setEnabled(false);
        removePassengerButton.setColorFilter(getResources().getColor(R.color.grey,null));
    }




    /**
     * Callback method for EditTextEnterEvent when enter is pressed
     * @param ctrl - reference to EditTextEnterEvent object (only have one right for this class right now)
     * @param text - text contained in EditText
     */
    @Override
    public void onImeBack(EditTextEnterEvent ctrl, String text) {
        if(!text.isEmpty()) {
            double cost = Double.parseDouble(text);
            controller.setParkingCost(cost);
            mListener.onParkingCostUpdated(cost);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_insurance:
                controller.onInsuranceCheckBoxChanged(insuranceCheckBox.isChecked());
                break;
            case R.id.cb_maintenance:
                controller.onMaintenanceCheckBoxChanged(maintenanceCheckBox.isChecked());
                break;
            case R.id.btn_detail_add_rider:
                controller.onAddPassengerButtonPressed();
                break;
            case R.id.btn_detail_remove_rider:
                controller.onRemovePassengerButtonPressed();
                break;
            case R.id.btn_hide_summary:
                // "Swipe"
                mListener.onDetailSummarySwipeDown(controller.getNumOfPassengers());
                break;
        }
    }




    // ----------------------------

    // TODO: add swipe up/down
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        return mDetector.onTouchEvent(event);
//    }
//
//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown: Pressed the view");
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            scrollDistanceY += distanceY;
//            if (scrollDistanceY > -1000) {
//                mListener.onDetailSummarySwipeDown();
//                // reset scroll distance
//                scrollDistanceY = 0;
//            }
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }
//
//    }
}
