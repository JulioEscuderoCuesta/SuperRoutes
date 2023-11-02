package com.example.superroutes.model;

public class RouteInformation {

    private String name;
    private String location;
    private String difficulty;
    private String imageURL;

    public RouteInformation() {

    }
    public RouteInformation(String name, String location, String difficulty, String imageURL) {
        this.name = name;
        this.location = location;
        this.difficulty = difficulty;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getLocation() { return location; }
    public String getDifficulty() { return difficulty; }

    public String getImageURL() {return imageURL;}

    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

}
