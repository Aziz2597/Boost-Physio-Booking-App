package com.boostphysio;

public abstract class Person {
    private int id;
    private String fullName;
    private String address;
    private String phoneNumber;
    
    public Person(int id, String fullName, String address, String phoneNumber) {
        this.id = id;
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and setters
    public int getId() { return id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + fullName + ", Phone: " + phoneNumber;
    }
}