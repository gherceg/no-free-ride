package com.graham.nofreeride.fragments.summary;

import android.content.Context;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.graham.nofreeride.activities.HomeActivity;
import com.graham.nofreeride.fragments.home.HomeFragment;
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
        void onSummarySwipeUp(double distance, int passengers);
    }

    View view;
    View statsContainer;

    TextView pricePerRiderTextView;
    TextView distanceTextView;
    TextView numOfPassengersTextView;

    ImageButton addPassengerButton;
    ImageButton removePassengerButton;
    ImageButton showDetailSummaryButton;

    SummaryController controller;
    private SummaryFragmentListener mListener;

    private ArrayList<LatLng> mLocations;

    // TODO: add swipe gesture
    private GestureDetector mDetector;

    public static SummaryFragment newInstance(double distance, ArrayList<LatLng> locations) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("locations",locations);
        args.putDouble("distance", distance);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onAttach passes in the context, which should implement the listener interface
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SummaryFragment.SummaryFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SummaryFragmentListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Update parking costs
        double parking  = ((HomeActivity) getActivity()).getParkingCost();
        controller.setParkingCost(parking);

        // update number of passengers
        int numOfPassengers = ((HomeActivity) getActivity()).getNumOfPassengers();
        controller.setNumOfPassengers(numOfPassengers);

        // refresh loads shared preferences
        controller.refreshSharedPreferences();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        mLocations = args.getParcelableArrayList("locations");
        double distance = args.getDouble("distance", 0.0);

        int passengers = ((HomeActivity)getActivity()).getNumOfPassengers();
        double parkingCost = ((HomeActivity)getActivity()).getParkingCost();
        // setup controller
        controller = new SummaryController(this,getContext(),distance,passengers,parkingCost);

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
            actionBar.setElevation(4);
        }


        pricePerRiderTextView = (TextView)view.findViewById(R.id.tv_price_per_rider);
        String price = String.format(Locale.US,"$%.2f",controller.getPricePerRider());
        pricePerRiderTextView.setText(price);


        distanceTextView = (TextView)view.findViewById(R.id.tv_total_distance);
        String distance = String.format(Locale.US, "%.3f miles",controller.getDistance());
        distanceTextView.setText(distance);


        numOfPassengersTextView = (TextView)view.findViewById(R.id.tv_num_of_passengers);
        controller.getNumOfPassengers();
//        updateNumberOfPassengers(Integer.toString(1));

        addPassengerButton = (ImageButton) view.findViewById(R.id.btn_add_rider);
        addPassengerButton.setOnClickListener(this);

        removePassengerButton = (ImageButton)view.findViewById(R.id.btn_remove_rider);
        removePassengerButton.setOnClickListener(this);

        showDetailSummaryButton = (ImageButton)view.findViewById(R.id.btn_show_detail_summary);
        showDetailSummaryButton.setOnClickListener(this);

        // setup map fragment
        // request callback when map is ready to be used
        // NOTE: be sure to use child fragment manager since this is a fragment within a fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // TODO: implement swipe up/down for summary page
//        statsContainer = (View)view.findViewById(R.id.view_stats_container);
//        mDetector = new GestureDetector(getContext(), new GestureListener());
//        statsContainer.setOnTouchListener(this);

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
    public void enableAddPassengersButton() {
        addPassengerButton.setEnabled(true);
        addPassengerButton.setColorFilter(getResources().getColor(R.color.white,null));
    }

    @Override
    public void disableAddPassengersButton() {
        addPassengerButton.setEnabled(false);
        addPassengerButton.setColorFilter(getResources().getColor(R.color.grey,null));
    }

    @Override
    public void disableRemovePassengerButton() {
        removePassengerButton.setEnabled(false);
        removePassengerButton.setColorFilter(getResources().getColor(R.color.grey,null));
//        removePassengerButton.setBackground(getResources().getDrawable(R.drawable.ic_remove_grey_24dp, null));
    }

    @Override
    public void enableRemovePassengersButton() {
        removePassengerButton.setEnabled(true);
        removePassengerButton.setColorFilter(getResources().getColor(R.color.white,null));
//        removePassengerButton.setBackground(getResources().getDrawable(R.drawable.ic_remove_white_24dp,null));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // setup initial view of map, should be based on locations received from drive
        
        // Need to check our passed in array
        if(mLocations == null || mLocations.size() <= 0) {
            // we can't draw anything
            Log.d(TAG, "onMapReady: should check for this before reaching this page");
        } else {
            LatLng start = mLocations.get(0);
            LatLng end = mLocations.get(mLocations.size() - 1);
            googleMap.addMarker(new MarkerOptions().position(start)
                    .title("Start"));

            googleMap.addMarker(new MarkerOptions().position(end)
                    .title("End"));

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .addAll(mLocations));
            // Store a data object with the polyline, used here to indicate an arbitrary type.
            polyline1.setTag("Drive");
            // style it?
            polyline1.setStartCap(new RoundCap());
            polyline1.setEndCap(new RoundCap());
            polyline1.setWidth(10);
            polyline1.setColor(R.color.colorAccent);
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
            case R.id.btn_show_detail_summary:
                mListener.onSummarySwipeUp(controller.getDistance(),controller.getNumOfPassengers());
                break;
        }
    }

    // TODO: implement swipe up/down for summary page
    // ------- FOR FUTURE IMPLEMENTATION
    // pass onto gesture detector
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        if(v.getId() == R.id.view_stats_container) {
//            return mDetector.onTouchEvent(event);
//        }
//        return false;
//    }
//
//
//    // Hanlding gestures
//    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown: Pressed the view");
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.d(TAG, "onScroll: Scrolling with x: " + distanceX + " and y: " + distanceY);
//            scrollDistanceY += distanceY;
//            if(scrollDistanceY > 1000) {
//                mListener.onSummarySwipeUp(mDistance,controller.numOfPassengers);
//                // reset scroll distance
//                scrollDistanceY = 0;
//            }
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.d(TAG, "onFling: Flung with velocity " + velocityX + " and " + velocityY);
//
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//    }

}
