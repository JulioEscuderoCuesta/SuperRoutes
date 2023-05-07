package com.example.superroutes.model;


import java.io.Serializable;
import java.util.ArrayList;


public class RouteProposal implements Serializable {

    private String routeId;
    private String whichDay;
    private int maxParticipants;

    private RouteProposalState routeProposalState;

    private ArrayList<String> participantsIds;
    private String comments;
    private String idGuide;

    public RouteProposal() {

    }
    public RouteProposal(String routeId, String whichDay, int maxParticipants, String comments, String idGuide) {
        this.routeId = routeId;
        this.whichDay = whichDay;
        this.maxParticipants = maxParticipants;
        routeProposalState = RouteProposalState.PROPOSED;
        participantsIds = new ArrayList<>();
        this.comments = comments;
        this.idGuide = idGuide;
    }

    public String getRouteId() { return routeId; }

    public String getWhichDay() {
        return whichDay;
    }

    public RouteProposalState getRouteProposalState() {
        return routeProposalState;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }
    public ArrayList<String> getParticipantsIds() { return participantsIds; }
    public String getComments() { return comments; }
    public String getIdGuide() { return idGuide; }

    public void setRouteId(String routeId) { this.routeId = routeId; }
    /*public void setWhichDay(LocalDate whichDay) {
        this.whichDay = whichDay;
    }*/
    public void setWhichDay(String whichDay) {
        this.whichDay = whichDay;
    }

    public void setRouteProposalState(RouteProposalState routeProposalState) {
        this.routeProposalState = routeProposalState;
    }

    public void setComments(String comments) { this.comments = comments; }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
    }

    public void setIdGuide(String idGuide) { this.idGuide = idGuide; }

    public void addParticipantId(String participantsId) {
        participantsIds.add(participantsId);
    }


}
