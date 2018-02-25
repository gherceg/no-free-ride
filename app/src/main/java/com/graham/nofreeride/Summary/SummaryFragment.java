package com.graham.nofreeride.Summary;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.graham.nofreeride.R;
import com.graham.nofreeride.riders.RidersFragment;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryFragment extends Fragment implements SummaryContract.view {
    View view;
    TextView pricePerRiderTextView;
    TextView distanceTextView;

    private double mPricePerRider;
    private double mDistance;


    public static SummaryFragment newInstance(double pricePerRider, double distance) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putDouble("price_per_rider", pricePerRider);
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
        String distance = String.format(Locale.US, "%.4f miles",mDistance);
        distanceTextView.setText(distance);

        return view;
    }
}
