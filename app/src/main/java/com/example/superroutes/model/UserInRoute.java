package com.example.superroutes.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserInRoute {

    private String name;
    private double latitude;
    private double longitude;
    private boolean fall;

    private UserInRoute() {

    }

    public UserInRoute(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fall = false;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean getFall() {
        return fall;
    }

    public void setName(String name) { this.name = name; }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFall(boolean fall) {
        this.fall = fall;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("fall", fall);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        UserInRoute that = (UserInRoute) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.latitude, that.latitude)
                && Objects.equals(this.longitude, that.longitude);
    }
}
