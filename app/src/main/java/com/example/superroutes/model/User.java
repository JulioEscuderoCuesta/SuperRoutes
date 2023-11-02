package com.example.superroutes.model;
import com.google.firebase.database.Exclude;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {

    private String name;
    private String surname;
    private String email;
    private Rol rol;
    private String telephoneNumber;
    private ArrayList<String> listOfGroups;
    private String imageURL;

    public User() {

    }
    public User(String name, String surname, String email, String telephoneNumber, String imageURL) {
        this.name = name;
        this.email = email;
        this.surname = surname;
        this.rol = Rol.SENDERIST;
        this.telephoneNumber = telephoneNumber;
        this.imageURL = imageURL;
        listOfGroups = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSurname() {
        return surname;
    }
    public Rol getRol() {
        return rol;
    }
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public ArrayList<String> getListOfGroups() {
        return listOfGroups;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }
    public void setListOfGroups(ArrayList<String> listOfGroups) {
        this.listOfGroups = listOfGroups;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.surname, that.surname)
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.telephoneNumber, that.telephoneNumber);
    }



}
