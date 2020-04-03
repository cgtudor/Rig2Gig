package com.gangoffive.rig2gig.advert.management;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GooglePlacesAPIReader
{
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    /**
     * Method makes a HTTP request to Google API, fetches all responses in StringBuilder.
     * Converts into a String then parses String data in Json and all
     * addresses in ArrayList and returns the ArrayList.
     * @param input
     * @return
     */
    public ArrayList<String> autoComplete(String input)
    {
        ArrayList<String> addresses = new ArrayList<>();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();

        try
        {
            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            sb.append("input=" + input);
            sb.append("&key=AIzaSyB4sMbIQwQaO0qlPHRuRCxB5--Zx54ACTE");
            URL url = new URL(sb.toString());
            connection = (HttpURLConnection)url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff = new char[1024];
            while((read = inputStreamReader.read(buff)) != -1)
            {
                jsonResult.append(buff, 0, read);
            }
        }
        catch(MalformedURLException mue)
        {
            Log.d(TAG, mue.toString());
        }
        catch(IOException io)
        {
            Log.d(TAG, io.toString());
        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }
        }

        try
        {
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray prediction = jsonObject.getJSONArray("predictions");

            for (int i = 0; i < prediction.length(); i++)
            {
                addresses.add(prediction.getJSONObject(i).getString("description"));
            }
        }
        catch(JSONException json)
        {
            Log.d(TAG, json.toString());
        }

        return addresses;
    }
}
