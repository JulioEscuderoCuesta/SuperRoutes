package com.example.superroutes.model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Route implements Serializable{

    private String name;
    private String location;
    private Difficulty difficulty;
    private double durationInHours;
    private String imageURL;

    public Route() {

    }

    public Route(String name, String location, Difficulty difficulty, double durationInHours, String imageURL) {
        this.name = name;
        this.location = location;
        this.difficulty = difficulty;
        this.durationInHours = durationInHours;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getLocation() { return location; }
    public Difficulty getDifficulty() { return difficulty; }

    public double getDurationInHours() {
        return durationInHours;
    }
    public String getImageURL() {return imageURL;}

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }

    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public void setDurationInHours(double durationInHours) {
        this.durationInHours = durationInHours;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Route that = (Route) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.location, that.location)
                && Objects.equals(this.difficulty, that.difficulty)
                && Objects.equals(this.durationInHours, that.durationInHours);
    }
}
