package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class Doctor {
    private int id;
    private String name;
    private String specialty;

    // Constructor
    public Doctor(int id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    // Constructor không có tham số
    public Doctor() {
    }

    public Doctor(String name, String specialty){
        this.name = name;
        this.specialty = specialty;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    // method toString
    public String toString() {
        return "Doctor [ID: " + id + ", Name: " + name + ", Specialty: " + specialty + "]";
    }
}
