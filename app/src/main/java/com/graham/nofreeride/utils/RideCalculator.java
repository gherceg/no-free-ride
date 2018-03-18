package com.graham.nofreeride.utils;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/3/18.
 */

public class RideCalculator {

    /**
     * Calculate the distance between two Lat,Long points
     * @param startLoc - Starting location object
     * @param endLoc - Ending location object
     * @return - the distance between two points IN MILES
     */
    public static double calculateDistance(LatLng startLoc, LatLng endLoc) {
        double R = 6371000;
        double distance = -1;

        // calculate distance
        double latOne = startLoc.latitude * (Math.PI / 180);
        double latTwo = endLoc.latitude * (Math.PI / 180);
        double latDiff = (startLoc.latitude - endLoc.latitude) * (Math.PI / 180);
        double lonDiff = (startLoc.longitude - endLoc.longitude) * (Math.PI / 180);

        double a = (Math.sin(latDiff/2) * Math.sin(latDiff/2)) + (Math.cos(latOne) * Math.cos(latTwo)) * (Math.sin(lonDiff/2) * Math.sin(lonDiff/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = R * c;
        // convert distance to miles
         return distance * 0.000621371;
    }

    /**
     * Used to calculate the price each rider should pay
     * @param numOfRiders - number of riders in the car, EXCLUDING the driver
     * @param milesPerGallon - the car's average miles per gallon
     * @param gasPricePerGallon - gas price per gallon for the current area
     * @param milesDriven - the total distance driven in miles
     * @return - the cost per rider
     */
    public static double calculatePricePerRider(int numOfRiders, double milesPerGallon, double gasPricePerGallon, double milesDriven) {
        Log.d(TAG, "pricePerRider: Calculating price for: " + numOfRiders + " riders, " + milesPerGallon + " MPG"  +
        gasPricePerGallon + " PPG," + milesDriven + " miles driven");
        // calculate total cost
        double totalCost = (milesDriven/milesPerGallon) * gasPricePerGallon;
        // add one to include the driver, break this out to the user?
        return totalCost / (numOfRiders + 1);

    }

}
