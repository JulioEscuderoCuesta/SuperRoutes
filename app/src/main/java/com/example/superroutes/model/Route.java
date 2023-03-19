package com.example.superroutes.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Route implements Serializable{

    private String name;
    private LocalDate whichDay;
    private Difficulty difficulty;
    private int maxParticipants;
    private ArrayList<User> participants;
    private double durationInHours;
    private User guide;

    private Route() {

    }

    public Route(String name, LocalDate whichDay, Difficulty difficulty, int maxParticipants, double durationInHours, User guide) {
        this.name = name;
        this.whichDay = whichDay;
        this.difficulty = difficulty;
        this.maxParticipants = maxParticipants;
        participants = new ArrayList<>();
        this.durationInHours = durationInHours;
        this.guide = guide;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDay() {
        return whichDay;
    }

    public Difficulty getDifficulty() { return difficulty; }

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

    private void setName(String name) { this.name = name; }
    public void setDay(LocalDate whichDay) {
        this.whichDay = whichDay;
    }

    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

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
        result.put("name", name);
        result.put("whichDay", whichDay.toString());
        result.put("difficulty", difficulty);
        result.put("maxParticipants", maxParticipants);
        result.put("durationInHours", durationInHours);
        result.put("guide", guide);

        return result;
    }

}
