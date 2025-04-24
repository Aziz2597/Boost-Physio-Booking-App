package com.boostphysio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
    
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("EEEE d MMMM yyyy, HH:mm");
    
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = true;
    }
    
    // Getters and setters
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public String getFormattedTimeRange() {
        return startTime.format(formatter) + "-" + 
               endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    @Override
    public String toString() {
        return getFormattedTimeRange() + (isAvailable ? " (Available)" : " (Booked)");
    }
}