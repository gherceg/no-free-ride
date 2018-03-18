package com.graham.nofreeride.fragments.summary;

import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.graham.nofreeride.R;
import com.graham.nofreeride.utils.ParcelableLocations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/10/18.
 */

public class SummaryFragment extends Fragment implements SummaryContract.view, OnMapReadyCallback, View.OnClickListener {


    public interface SummaryFragmentListener {

    }

    View view;
    View statsContainer;
    TextView pricePerRiderTextView;
    TextView distanceTextView;
    TextView numOfPassengersTextView;
    ImageButton addPassengerButton;
    ImageButton removePassengerButton;

    SummaryController controller;

    private double mPricePerRider;
    private double mDistance;
    private ArrayList<LatLng> mLocations;


    public static SummaryFragment newInstance(double distance, ArrayList<LatLng> locations) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("locations",locations);
        args.putDouble("price_per_rider", 0);
        args.putDouble("distance", distance);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        mLocations = args.getParcelableArrayList("locations");
        mPricePerRider = args.getDouble("price_per_rider", 0.0);
        mDistance = args.getDouble("distance", 0.0);
//        mDistance = 15.241;

        // setup controller
        controller = new SummaryController(this,getContext(),mDistance);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
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

        statsContainer = (View)view.findViewById(R.id.view_stats_container);

        numOfPassengersTextView = (TextView)view.findViewById(R.id.tv_num_of_passengers);
        updateNumberOfPassengers(Integer.toString(1));

        addPassengerButton = (ImageButton) view.findViewById(R.id.btn_add_rider);
        addPassengerButton.setOnClickListener(this);
        removePassengerButton = (ImageButton)view.findViewById(R.id.btn_remove_rider);
        removePassengerButton.setOnClickListener(this);

        // setup map fragment
        // request callback when map is ready to be used
        // NOTE: be sure to use child fragment manager since this is a fragment within a fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void updatePriceTextView(String price) {
        pricePerRiderTextView.setText(price);
    }

    @Override
    public void updateNumberOfPassengers(String passengers) {
        numOfPassengersTextView.setText(passengers);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // setup initial view of map, should be based on locations received from drive
//        LatLng miami = new LatLng(25.76, -80.1918);
        // Need to check our passed in array
        if(mLocations == null || mLocations.size() <= 0) {
            // we can't draw anything
        } else {
            LatLng start = mLocations.get(0);
            googleMap.addMarker(new MarkerOptions().position(start)
                    .title("Marker in Miami"));
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(mLocations));
            // Store a data object with the polyline, used here to indicate an arbitrary type.
            polyline1.setTag("Drive");
            // style it?
            polyline1.setEndCap(new RoundCap());
            polyline1.setWidth(10);
            polyline1.setColor(R.color.colorPrimary);
            polyline1.setJointType(JointType.ROUND);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start,14.0f));
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_add_rider:
                Log.d(TAG, "onClick: Add a passenger!");
                controller.addPassengerPressed();
                break;
            case R.id.btn_remove_rider:
                Log.d(TAG, "onClick: Remove a passenger!");
                controller.removePassengerPressed();
                break;
        }
    }

}
