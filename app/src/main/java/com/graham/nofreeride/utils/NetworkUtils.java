package com.graham.nofreeride.utils;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by grahamherceg on 2/14/18.
 */

public class NetworkUtils {
    final static String GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    final static String GOOGLE_MAPS_ROADS_URL = "https://roads.googleapis.com/v1/snapToRoads?parameters";
    final static String GOOGLE_MAPS_API_KEY = ""; // took out api key

//    public static ArrayList<LatLng> getSnappedLocations(ArrayList<LatLng> latLngs) {
//
//    }

    // old method for finding distance via roads from starting to end location
    public static URL buildUrl(Location locationOne, Location locationTwo) {
        String paramOne = "origin=" + Double.toString(locationOne.getLatitude()) + "," + Double.toString(locationOne.getLongitude());
        String paramTwo = "&destination=" + Double.toString(locationTwo.getLatitude()) + "," + Double.toString(locationTwo.getLongitude());
        String key = "&key=" + GOOGLE_MAPS_API_KEY;
        String path = paramOne + paramTwo + key;
        Uri builtUri = Uri.parse(GOOGLE_MAPS_URL).buildUpon()
                .appendPath(path)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getMapsResponseFromHttpUrl(URL url) throws IOException {

        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch ( Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }

//    private static String buildRoadsParam(ArrayList<LatLng> latLngs) {
//        String url = "";
//        // can estimate size by saying each latlng pair takes up roughly 16 chars
//        int size = latLngs.size() * 16;
//        StringBuilder pointsBuilder = new StringBuilder(size);
//        StringBuilder individualPointBuilder = new StringBuilder(20);
//        for(int i = 0; i < latLngs.size(); i++) {
//            if(latLngs.get(i) != null) {
//                individualPointBuilder.append(Double.toString(latLngs.get(i).latitude));
//                individualPointBuilder.append(",");
//                individualPointBuilder.append(Double.toString(latLngs.get(i).longitude));
//                // as long as it is not the last element in array, add a pipe in between params
//                if(i != latLngs.size() - 1) {
//                    individualPointBuilder.append("|");
//                }
//
//                pointsBuilder.append(individualPointBuilder.toString());
//            } else {
//                Log.d(TAG, "buildRoadsParam: Invalid latLng object. Not adding to params.");
//            }
//        }
//        String points = pointsBuilder.toString();
//        StringBuilder urlBuilder = new StringBuilder(points.length() + 20);
//        urlBuilder.append(GOOGLE_MAPS_ROADS_URL);
//
//
//
//        // build the rest of the url
//    }
}
