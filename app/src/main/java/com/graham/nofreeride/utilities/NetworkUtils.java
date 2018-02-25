package com.graham.nofreeride.utilities;

import android.location.Location;
import android.net.Uri;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by grahamherceg on 2/14/18.
 */

public class NetworkUtils {
    final static String GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    final static String GOOGLE_MAPS_API_KEY = ""; // took out api key


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
}
