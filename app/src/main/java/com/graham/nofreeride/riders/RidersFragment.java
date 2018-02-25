package com.graham.nofreeride.riders;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.graham.nofreeride.R;
import com.graham.nofreeride.Summary.SummaryFragment;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class RidersFragment extends Fragment implements View.OnClickListener, RidersContract.view {

    RidersController controller;

    View view;

    NumberPicker numberPicker;
    Button calculateButton;

    private double mDistance;


    public static RidersFragment newInstance(double distance) {
        RidersFragment fragment = new RidersFragment();
        Bundle args = new Bundle();
        args.putDouble("distance", distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        mDistance = args.getDouble("distance", 0.0);

        // create controller
        controller = new RidersController(this,getContext(),mDistance);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_riders,container, false);

        numberPicker = (NumberPicker)view.findViewById(R.id.np_riders);
        calculateButton = (Button)view.findViewById(R.id.btn_calculate_price);
        calculateButton.setOnClickListener(this);

        //setup number picker
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(8);
        numberPicker.setWrapSelectorWheel(true);


        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_calculate_price:
                int numOfRiders = numberPicker.getValue();
                controller.onCalculateButtonPressed(numOfRiders);
                break;
        }
    }

    @Override
    public void showSummaryFragment(double pricePerRider) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frag_container, SummaryFragment.newInstance(pricePerRider, mDistance)).addToBackStack(null).commit();

    }



}
