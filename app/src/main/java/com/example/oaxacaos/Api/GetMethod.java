package com.example.oaxacaos.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.oaxacaos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetMethod {

    HttpURLConnection client;

    public JSONArray makeRequest(String route, JSONObject jsonParam, Context context, boolean authenticate) {

        String baseURL = context.getResources().getString(R.string.base_url);
        JSONArray json = null;
        try {
            URL url = new URL(baseURL + route
                    + "latitude=" + jsonParam.getDouble("latitude")
                    + "&longitude=" + jsonParam.getDouble("longitude"));
            String token = context.getSharedPreferences("Auth", 0).getString("token", null);
            client = (HttpURLConnection) url.openConnection();
            if (token != null && authenticate) {
                client.setRequestProperty("X-Auth-Token", token);
            }

            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                json = new JSONArray(response.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) // Make sure the connection is not null.
                client.disconnect();
        }
        return json;
    }

}
