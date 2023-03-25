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

    public LocalDate getWhichDay() {
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

    public void setName(String name) { this.name = name; }
    public void setWhichDay(String whichDayString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.whichDay = LocalDate.parse(whichDayString, formatter);
    }

    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /*public void setParticipants(HashMap<String, Object> participantsWithId) {
        for(Object o : participantsWithId.values()) {
            User senderist = (User)o;
            participants.add(senderist);
        }
    }*/

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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Route that = (Route) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.whichDay, that.whichDay)
                && Objects.equals(this.guide.getEmail(), that.guide.getEmail());
    }
}
