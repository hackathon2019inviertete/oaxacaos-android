package com.example.oaxacaos.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class Reports {

    private double latitude, longitude;
    private JSONArray likes;
    private int reportType, status;
    private String address, userId, CreatedAt, UpdatedAt;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLikes(JSONArray likes) {
        this.likes = likes;
    }
    public void setReportType(int reportType) {
        this.reportType = reportType;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }
    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public JSONArray getLikes() {
        return likes;
    }
    public int getReportType() {
        return reportType;
    }
    public String getAddress() {
        return address;
    }
    public String getUserId() {
        return userId;
    }
    public String getCreatedAt() {
        return CreatedAt;
    }
    public String getUpdatedAt() {
        return UpdatedAt;
    }
}
