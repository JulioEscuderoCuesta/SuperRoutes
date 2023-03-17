package com.example.superroutes.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Route implements Serializable{

    private String day;
    private int maxParticipants;
    private ArrayList<User> participants;
    private double durationInHours;
    private User guide;

    private Route() {

    }

    public Route(String day, int maxParticipants, double durationInHours, User guide) {
        this.day = day;
        this.maxParticipants = maxParticipants;
        participants = new ArrayList<>();
        this.durationInHours = durationInHours;
        this.guide = guide;
    }


    public String getDay() {
        return day;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public ArrayList<User> getParticiPants() {
        return participants;
    }

    public double getDurationInHours() {
        return durationInHours;
    }

    public User getGuide() {
        return guide;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public void setDurationInHours(double durationInHours) {
        this.durationInHours = durationInHours;
    }

    public void setGuide(User guide) {
        this.guide = guide;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("day", day);
        result.put("maxParticipants", maxParticipants);
        result.put("participants", participants);
        result.put("durationInHours", durationInHours);
        result.put("guide", guide);

        return result;
    }

}
