package com.example.superroutes.model;
import com.google.firebase.database.Exclude;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {

    private String name;
    private String email;
    private String telephoneNumber;

    public User() {

    }
    public User(String name, String email, String telephoneNumber) {
        this.name = name;
        this.email = email;
        this.telephoneNumber = telephoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("telephoneNumber", telephoneNumber);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.email, that.email)
                && Objects.equals(this.telephoneNumber, that.telephoneNumber);
    }



}
