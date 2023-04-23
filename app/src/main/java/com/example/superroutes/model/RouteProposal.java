package com.example.superroutes.model;

import com.google.firebase.database.Exclude;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RouteProposal{

    private String routeId;
    private LocalDate whichDay;
    private int maxParticipants;

    private RouteProposalState routeProposalState;

    private ArrayList<User> participants;
    private String comments;
    private User guide;

    private RouteProposal() {

    }
    public RouteProposal(String routeId, LocalDate whichDay, int maxParticipants, String comments, User guide) {
        this.routeId = routeId;
        this.whichDay = whichDay;
        this.maxParticipants = maxParticipants;
        routeProposalState = RouteProposalState.PROPOSED;
        participants = new ArrayList<>();
        this.comments = comments;
        this.guide = guide;
    }

    public String getRouteId() { return routeId; }

    public LocalDate getWhichDay() {
        return whichDay;
    }

    public RouteProposalState getRouteProposalState() {
        return routeProposalState;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }
    public ArrayList<User> getParticipants() { return participants; }
    public String getComments() { return comments; }
    public User getGuide() { return guide; }

    public void setRouteId(String routeId) { this.routeId = routeId; }
    /*public void setWhichDay(LocalDate whichDay) {
        this.whichDay = whichDay;
    }*/
    public void setWhichDay(String whichDayString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.whichDay = LocalDate.parse(whichDayString, formatter);
    }

    public void setRouteProposalState(RouteProposalState routeProposalState) {
        this.routeProposalState = routeProposalState;
    }

    public void setComments(String comments) { this.comments = comments; }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    public void addParticipant(User participant) {
        participants.add(participant);
    }
    public void setGuide(User guide) { this.guide = guide; }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("routeId", routeId);
        result.put("whichDay", whichDay.toString());
        result.put("routeProposalState", routeProposalState);
        result.put("maxParticipants", maxParticipants);
        result.put("comments", comments);
        result.put("guide", guide);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        RouteProposal that = (RouteProposal) o;
        return Objects.equals(this.routeId, that.routeId);
    }
}
