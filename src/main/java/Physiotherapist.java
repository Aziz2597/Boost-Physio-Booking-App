package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Physiotherapist extends Person {
    private List<String> expertiseAreas;
    private List<Treatment> treatments;
    private HashMap<String, List<TimeSlot>> timetable; // key: date in "yyyy-MM-dd" format
    
    public Physiotherapist(int id, String fullName, String address, String phoneNumber) {
        super(id, fullName, address, phoneNumber);
        this.expertiseAreas = new ArrayList<>();
        this.treatments = new ArrayList<>();
        this.timetable = new HashMap<>();
    }
    
    // Expertise area methods
    public void addExpertiseArea(String area) {
        expertiseAreas.add(area);
    }
    
    public List<String> getExpertiseAreas() {
        return new ArrayList<>(expertiseAreas);
    }
    
    public boolean hasExpertise(String area) {
        return expertiseAreas.contains(area);
    }
    
    // Treatment methods
    public void addTreatment(Treatment treatment) {
        if (hasExpertise(treatment.getExpertiseArea())) {
            treatments.add(treatment);
        } else {
            throw new IllegalArgumentException("Physiotherapist does not have expertise in " 
                + treatment.getExpertiseArea());
        }
    }
    
    public List<Treatment> getTreatments() {
        return new ArrayList<>(treatments);
    }
    
    public Treatment getTreatmentByName(String name) {
        return treatments.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    
    // Timetable methods
    public void addTimeSlot(String date, TimeSlot slot) {
        timetable.computeIfAbsent(date, k -> new ArrayList<>()).add(slot);
    }
    
    public List<TimeSlot> getAvailableSlotsForDate(String date) {
        List<TimeSlot> slots = timetable.getOrDefault(date, new ArrayList<>());
        return slots.stream()
                .filter(TimeSlot::isAvailable)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public HashMap<String, List<TimeSlot>> getTimetable() {
        return new HashMap<>(timetable);
    }
    
    @Override
    public String toString() {
        return super.toString() + ", Expertise: " + String.join(", ", expertiseAreas);
    }
}