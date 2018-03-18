package com.graham.nofreeride.utils;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by grahamherceg on 3/18/18.
 */

public class ParcelableLocations implements Parcelable {

    List<Location> locations;


    public ParcelableLocations(List<Location> locations) {
        this.locations = locations;
    }

    private ParcelableLocations(Parcel parcel) {
        this.locations = new ArrayList<>();
        parcel.readTypedList(locations, Location.CREATOR);
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(locations);
    }

    public static final Parcelable.Creator<ParcelableLocations> CREATOR
            = new Parcelable.Creator<ParcelableLocations>() {

                @Override
                public ParcelableLocations createFromParcel(Parcel source) {
                    return new ParcelableLocations(source);
                }

                @Override
                public ParcelableLocations[] newArray(int size) {
                    return new ParcelableLocations[size];
                }
            };
}
