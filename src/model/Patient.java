package model;

public class Patient {
    private int patientID;
    private String name;
    private String phone;

    public Patient(int patientID, String name, String phone) {
        this.patientID = patientID;
        this.name = name;
        this.phone = phone;
    }

    public Patient(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public int getPatientID() {
        return patientID;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        if (patientID > 0) {
            return "Patient ID= " + patientID + ", Name= " + name + ", Phone= " + phone;
        } else {
            return "Name: " + name + ", Phone: " + phone;
        }
    }
}