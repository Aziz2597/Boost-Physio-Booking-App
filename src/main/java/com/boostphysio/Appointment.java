package com.boostphysio;


import java.time.LocalDateTime;

public class Appointment {
    public enum Status {
        BOOKED, CANCELLED, ATTENDED
    }
    
    private int id;
    private Physiotherapist physiotherapist;
    private Patient patient;
    private Treatment treatment;
    private TimeSlot timeSlot;
    private Status status;
    
    public Appointment(int id, Physiotherapist physiotherapist, Patient patient, 
                      Treatment treatment, TimeSlot timeSlot) {
        this.id = id;
        this.physiotherapist = physiotherapist;
        this.patient = patient;
        this.treatment = treatment;
        this.timeSlot = timeSlot;
        this.status = Status.BOOKED;
        
        // Mark the time slot as unavailable
        timeSlot.setAvailable(false);
    }
    
    // Getters
    public int getId() { return id; }
    public Physiotherapist getPhysiotherapist() { return physiotherapist; }
    public Patient getPatient() { return patient; }
    public Treatment getTreatment() { return treatment; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    
    // Status methods
    public Status getStatus() { return status; }
    
    public void setStatus(Status status) {
        this.status = status;
        // If cancelled, free up the time slot
        if (status == Status.CANCELLED) {
            timeSlot.setAvailable(true);
        }
    }
    
    public void cancel() {
        setStatus(Status.CANCELLED);
    }
    
    public void markAsAttended() {
        setStatus(Status.ATTENDED);
    }
    
    @Override
    public String toString() {
        return "Appointment #" + id + ": " + patient.getFullName() + 
               " with " + physiotherapist.getFullName() +
               " for " + treatment.getName() + 
               " at " + timeSlot.getFormattedTimeRange() +
               " [" + status + "]";
    }
}