package com.graham.nofreeride.fragments.summary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.graham.nofreeride.R;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryFragment extends Fragment implements SummaryContract.view, NumberPicker.OnValueChangeListener {
    View view;
    TextView pricePerRiderTextView;
    TextView distanceTextView;
    NumberPicker numberRiderPicker;

    SummaryController controller;

    private double mPricePerRider;
    private double mDistance;


    public static SummaryFragment newInstance(double distance) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putDouble("price_per_rider", 0);
        args.putDouble("distance", distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        mPricePerRider = args.getDouble("price_per_rider", 0.0);
        mDistance = args.getDouble("distance", 0.0);
        mDistance = 15.241;

        // setup controller
        controller = new SummaryController(this,getContext(),mDistance);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary,container,false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }


        pricePerRiderTextView = (TextView)view.findViewById(R.id.tv_price_per_rider);
        String price = String.format(Locale.US,"$%.2f",mPricePerRider);
        pricePerRiderTextView.setText(price);

        distanceTextView = (TextView)view.findViewById(R.id.tv_total_distance);
        String distance = String.format(Locale.US, "%.3f miles",mDistance);
        distanceTextView.setText(distance);

        numberRiderPicker = (NumberPicker)view.findViewById(R.id.np_num_riders);

        //setup number picker
        numberRiderPicker.setMinValue(1);
        numberRiderPicker.setMaxValue(8);
        numberRiderPicker.setWrapSelectorWheel(true);
        numberRiderPicker.setOnValueChangedListener(this);
        

        return view;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch(picker.getId()) {
            case R.id.np_num_riders:
                controller.calculatePricePerRider(newVal);
                break;
        }

    }

    @Override
    public void updatePriceTextView(String price) {
        pricePerRiderTextView.setText(price);
    }

}
