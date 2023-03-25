package com.example.superroutes.model;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {

    private String nombre;
    private String email;
    private String telefono;
    private Rol rol;

    public User() {

    }
    public User(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        JSONObject json = new JSONObject();
        try {
            json.put("nombre", nombre);
            json.put("email", email);
            json.put("telefono", telefono);
        } catch (JSONException e) {
            //Mensaje de error
        }
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Rol getRol() {
        return rol;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }


}
