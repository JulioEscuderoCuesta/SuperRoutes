package com.example.superroutes.model;

public class Message {

    private String text;
    private long date;
    private String userId;

    public Message() {

    }

    public Message(String text, long date, String userId) {
        this.text = text;
        this.date = date;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public long getDate() {
        return date;
    }

    public String getUser() {
        return userId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setUser(String userId) {
        this.userId = userId;
    }
}
