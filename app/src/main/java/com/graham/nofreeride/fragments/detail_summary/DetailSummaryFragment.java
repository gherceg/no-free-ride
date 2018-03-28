package com.graham.nofreeride.fragments.detail_summary;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.graham.nofreeride.R;
import com.graham.nofreeride.fragments.summary.SummaryFragment;

import org.w3c.dom.Text;

import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 3/26/18.
 */

public class DetailSummaryFragment extends Fragment implements View.OnTouchListener {


    public interface DetailSummaryFragmentListener {
        void onDetailSummarySwipeDown();
    }

    View view;
    ImageButton removePassengerButton;
    ImageButton addPassengerButton;
    TextView numOfPassengersTextView;
    CheckBox maintenanceCheckBox;
    CheckBox insuranceCheckBox;
    TextView pricePerRiderTextView;


    private double mPricePerRider;
    private double mDistance;
    private int mNumOfPassengers;

    private GestureDetector mDetector;
    private double scrollDistanceY = 0;

    private DetailSummaryFragmentListener mListener;



    /**
     * Static initializer for DetailSummaryFragment
     * @return fragment - instance of the newly created DetailSummaryFragment
     */
    public static DetailSummaryFragment newInstance(double distance, int passengers) {
        DetailSummaryFragment fragment = new DetailSummaryFragment();
        Bundle args = new Bundle();
        args.putInt("passengers",passengers);
        args.putDouble("distance",distance);
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        mNumOfPassengers = args.getInt("passengers");
        mDistance = args.getDouble("distance");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_detail_summary,container,false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        mDetector = new GestureDetector(getContext(), new GestureListener());
        view.setOnTouchListener(this);

        numOfPassengersTextView = (TextView)view.findViewById(R.id.tv_detail_num_of_passengers);
        numOfPassengersTextView.setText(String.format(Locale.US, "%d",mNumOfPassengers));
        pricePerRiderTextView = (TextView)view.findViewById(R.id.tv_detail_price_per_rider);
        pricePerRiderTextView.setText(String.format(Locale.US, "%.2f",mPricePerRider));

        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG, "onDown: Pressed the view");
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollDistanceY += distanceY;
            if(scrollDistanceY > -1000) {
                mListener.onDetailSummarySwipeDown();
                // reset scroll distance
                scrollDistanceY = 0;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }
}
