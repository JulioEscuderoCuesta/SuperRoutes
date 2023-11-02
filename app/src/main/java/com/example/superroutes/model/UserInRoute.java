package com.example.superroutes.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserInRoute {

    private String name;
    public String imageURL;
    private double latitude;
    private double longitude;
    private boolean fall;
    private boolean accelerometerActivated;
    private UserInRoute() {

    }

    public UserInRoute(String name, String imageURL, double latitude, double longitude) {
        this.name = name;
        this.imageURL = imageURL;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fall = false;
        accelerometerActivated = true;
    }

    public String getName() {
        return name;
    }
    public String getImageURL() { return imageURL;}

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean getFall() {
        return fall;
    }

    public boolean getAccelerometerActivated() {
        return accelerometerActivated;
    }

    public void setName(String name) { this.name = name; }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFall(boolean fall) {
        this.fall = fall;
    }
    public void setAccelerometerActivated(boolean accelerometerActivated) { this.accelerometerActivated = accelerometerActivated;}
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
