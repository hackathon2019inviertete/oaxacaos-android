package com.example.oaxacaos.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

public class Reports {

    private double latitude, longitude;
    private JSONArray likes;
    private int reportType;
    private int status;
    private String report_id;
    private String address, userId, CreatedAt, UpdatedAt;
    String [] report_types = {"Semáforo", "Accidente", "Bloqueo", "Obstrucción"};

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
    public void setReport_id(String report_id) {
        this.report_id = report_id;
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
    public String getReport_id() {
        return report_id;
    }
    public String getReportType() {
        if (reportType < 4) {
            return report_types[reportType];
        }
        else {
            return "Desconocido";
        }
    }
}
