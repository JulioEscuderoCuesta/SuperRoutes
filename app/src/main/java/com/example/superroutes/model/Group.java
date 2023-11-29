package com.example.superroutes.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group implements Serializable{

    private String name;
    private List<String> listOfMembers;
    private String creationDate;
    private List<String> listOfIdsRoutesProposals;
    private String dateLastMessage;
    private String imageURL;

    private Group() {

    }

    public Group(String name, List<String> listOfMembers, String creationDate, String imageURL) {
        this.name = name;
        this.listOfMembers = listOfMembers;
        this.creationDate = creationDate;
        listOfIdsRoutesProposals = new ArrayList<>();
        this.imageURL = imageURL;

    }

    public String getName() {
        return name;
    }

    public List<String> getListOfMembers() { return listOfMembers; }
    public String getCreationDate() { return creationDate; }

    public List<String> getListOfIdsRoutesProposals() {
        return listOfIdsRoutesProposals;
    }

    public String getDateLastMessage() {
        return dateLastMessage;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setName(String name) { this.name = name; }
    public void setListOfMembers(List<String> listOfMembers) { this.listOfMembers = listOfMembers; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }


    public void setListOfIdsRoutesProposals(List<String> listOfIdsRoutesProposals) {
        this.listOfIdsRoutesProposals = listOfIdsRoutesProposals;
    }

    public void setDateLastMessage(String dateLastMessage) {
        this.dateLastMessage = dateLastMessage;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Group that = (Group) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.listOfMembers, that.listOfMembers)
                && Objects.equals(this.creationDate, that.creationDate)
                && Objects.equals(this.listOfIdsRoutesProposals, that.listOfIdsRoutesProposals);
    }
}
