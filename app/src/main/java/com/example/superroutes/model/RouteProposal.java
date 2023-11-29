package com.example.superroutes.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RouteProposal implements Serializable {

    private String routeId;
    private String whichDay;
    private int maxParticipants;
    private RouteProposalState routeProposalState;
    private ArrayList<String> participantsIds;
    private String comments;
    private String idGuide;
    private String idGroup;
    private Map<String, Boolean> votes;

    public RouteProposal() {

    }
    public RouteProposal(String routeId, String whichDay, int maxParticipants, String comments, String idGuide, String idGroup) {
        this.routeId = routeId;
        this.whichDay = whichDay;
        this.maxParticipants = maxParticipants;
        routeProposalState = RouteProposalState.PROPOSED;
        participantsIds = new ArrayList<>();
        this.comments = comments;
        this.idGuide = idGuide;
        this.idGroup = idGroup;
        votes = new HashMap<>();
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
    public String getIdGroup() {
        return idGroup;
    }

    public Map<String, Boolean> getVotes() {
        return votes;
    }

    public void setRouteId(String routeId) { this.routeId = routeId; }

    public void setWhichDay(String whichDay) {
        this.whichDay = whichDay;
    }

    public void setRouteProposalState(RouteProposalState routeProposalState) {
        this.routeProposalState = routeProposalState;
    }

    public void setComments(String comments) { this.comments = comments; }


    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
    public void setMaxParticipants(Object maxParticipants) {
        if(maxParticipants instanceof Long)
            this.maxParticipants = ((Long) maxParticipants).intValue();
        else if (maxParticipants instanceof Integer)
            this.maxParticipants = (Integer) maxParticipants;
        else if (maxParticipants instanceof String)
            try {
                this.maxParticipants = Integer.parseInt((String) maxParticipants);
            } catch (NumberFormatException e) {
                this.maxParticipants = (int) Long.parseLong((String) maxParticipants);
            }
    }

    public void setParticipantsIds(ArrayList<String> participantsIds) {
        this.participantsIds = participantsIds;
    }

    public void setIdGuide(String idGuide) { this.idGuide = idGuide; }

    public void setVotes(Map<String, Boolean> votes) {
        this.votes = votes;
    }

    public void addParticipantId(String participantsId) {
        participantsIds.add(participantsId);
    }


}
