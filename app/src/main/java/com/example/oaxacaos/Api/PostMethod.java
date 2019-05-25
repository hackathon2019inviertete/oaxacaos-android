package com.example.oaxacaos.Api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.oaxacaos.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostMethod {

    HttpURLConnection client;

    public JSONObject makeRequest(String route, JSONObject jsonParam, Context context, boolean authenticate) {

        String baseURL = context.getResources().getString(R.string.base_url);
        JSONObject json = null;
        try {
            URL url = new URL(baseURL + route);
            String token = context.getSharedPreferences("Auth", 0).getString("token", null);
            client = (HttpURLConnection) url.openConnection();
            client.setDoOutput(true);
            client.setDoInput(true);
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            if (token != null && authenticate) {
                client.setRequestProperty("Authorization", token);
            }
            client.setInstanceFollowRedirects(false);
            client.setUseCaches(false);

            DataOutputStream outputPost = new DataOutputStream(client.getOutputStream());
            byte[] bytes = jsonParam.toString().getBytes("UTF-8");
            outputPost.write(bytes);
            outputPost.flush();
            outputPost.close();

            if (client.getHeaderField("Authorization") != null) {
                Log.i("HEADER", client.getHeaderField("Authorization"));
                SharedPreferences sharedPreferences = context.getSharedPreferences("Auth", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", client.getHeaderField("Authorization"));
                editor.commit();
            }

            if (client.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                json = new JSONObject(response.toString());
                Log.i("MSGJSON", json.toString());
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
