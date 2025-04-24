package com.boostphysio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingSystem {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private Map<Integer, Appointment> appointments;
    private int nextAppointmentId;
    
    public BookingSystem() {
        physiotherapists = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new HashMap<>();
        nextAppointmentId = 1;
    }
    
    // Physiotherapist management
    public void addPhysiotherapist(Physiotherapist physio) {
        physiotherapists.add(physio);
    }
    
    public Physiotherapist getPhysiotherapistById(int id) {
        return physiotherapists.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public Physiotherapist getPhysiotherapistByName(String name) {
        return physiotherapists.stream()
                .filter(p -> p.getFullName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    public List<Physiotherapist> getPhysiotherapistsByExpertise(String expertiseArea) {
        return physiotherapists.stream()
                .filter(p -> p.hasExpertise(expertiseArea))
                .collect(Collectors.toList());
    }
    
    // Patient management
    public void addPatient(Patient patient) {
        if (getPatientById(patient.getId()) != null) {
            throw new IllegalArgumentException("Patient with ID " + patient.getId() + " already exists");
        }
        patients.add(patient);
    }
    
    public void removePatient(int id) {
        Patient patient = getPatientById(id);
        if (patient != null) {
            // First check for active appointments
            boolean hasActiveAppointments = appointments.values().stream()
                .anyMatch(a -> a.getPatient().getId() == id && a.getStatus() == Appointment.Status.BOOKED);
            
            if (hasActiveAppointments) {
                throw new IllegalStateException("Cannot remove patient with active appointments");
            }
            
            patients.remove(patient);
        } else {
            throw new IllegalArgumentException("Patient with ID " + id + " not found");
        }
    }
    
    public Patient getPatientById(int id) {
        return patients.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    // Appointment booking methods
    
    // Method 1: Book by expertise area
    public List<Map<String, Object>> searchAvailableSlotsByExpertise(String expertiseArea) {
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        
        // Find physiotherapists with the requested expertise
        List<Physiotherapist> qualifiedPhysios = getPhysiotherapistsByExpertise(expertiseArea);
        
        for (Physiotherapist physio : qualifiedPhysios) {
            // For each treatment in their expertise area
            for (Treatment treatment : physio.getTreatments().stream()
                                           .filter(t -> t.getExpertiseArea().equals(expertiseArea))
                                           .collect(Collectors.toList())) {
                
                // Check all available slots in their timetable
                for (Map.Entry<String, List<TimeSlot>> entry : physio.getTimetable().entrySet()) {
                    String date = entry.getKey();
                    
                    for (TimeSlot slot : entry.getValue()) {
                        if (slot.isAvailable()) {
                            // Create result map with all relevant information
                            Map<String, Object> slotInfo = new HashMap<>();
                            slotInfo.put("physiotherapist", physio);
                            slotInfo.put("treatment", treatment);
                            slotInfo.put("timeSlot", slot);
                            slotInfo.put("date", date);
                            
                            availableSlots.add(slotInfo);
                        }
                    }
                }
            }
        }
        
        return availableSlots;
    }
    
    // Method 2: Book by physiotherapist name
    public List<Map<String, Object>> searchAvailableSlotsByPhysiotherapist(String physioName) {
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        
        Physiotherapist physio = getPhysiotherapistByName(physioName);
        if (physio == null) {
            return availableSlots; // Empty list if physiotherapist not found
        }
        
        // For each treatment offered by this physiotherapist
        for (Treatment treatment : physio.getTreatments()) {
            // Check all available slots in their timetable
            for (Map.Entry<String, List<TimeSlot>> entry : physio.getTimetable().entrySet()) {
                String date = entry.getKey();
                
                for (TimeSlot slot : entry.getValue()) {
                    if (slot.isAvailable()) {
                        // Create result map with all relevant information
                        Map<String, Object> slotInfo = new HashMap<>();
                        slotInfo.put("physiotherapist", physio);
                        slotInfo.put("treatment", treatment);
                        slotInfo.put("timeSlot", slot);
                        slotInfo.put("date", date);
                        
                        availableSlots.add(slotInfo);
                    }
                }
            }
        }
        
        return availableSlots;
    }
    
    // Book appointment using a selected slot
    public Appointment bookAppointment(Patient patient, Physiotherapist physio, 
                                       Treatment treatment, TimeSlot slot) {
        if (!slot.isAvailable()) {
            throw new IllegalStateException("Selected time slot is not available");
        }
        
        Appointment appointment = new Appointment(nextAppointmentId++, physio, patient, treatment, slot);
        appointments.put(appointment.getId(), appointment);
        
        return appointment;
    }
    
    // Appointment management
    public void cancelAppointment(int appointmentId) {
        Appointment appointment = appointments.get(appointmentId);
        
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        
        if (appointment.getStatus() != Appointment.Status.BOOKED) {
            throw new IllegalStateException("Only booked appointments can be cancelled");
        }
        
        appointment.cancel();
    }
    
    public Appointment rescheduleAppointment(int appointmentId, TimeSlot newSlot) {
        Appointment oldAppointment = appointments.get(appointmentId);
        
        if (oldAppointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        
        if (oldAppointment.getStatus() != Appointment.Status.BOOKED) {
            throw new IllegalStateException("Only booked appointments can be rescheduled");
        }
        
        if (!newSlot.isAvailable()) {
            throw new IllegalStateException("New time slot is not available");
        }
        
        // Create new appointment
        Appointment newAppointment = new Appointment(
            nextAppointmentId++,
            oldAppointment.getPhysiotherapist(),
            oldAppointment.getPatient(),
            oldAppointment.getTreatment(),
            newSlot
        );
        
        // Cancel old appointment
        oldAppointment.cancel();
        
        // Add new appointment
        appointments.put(newAppointment.getId(), newAppointment);
        
        return newAppointment;
    }
    
    public void markAppointmentAsAttended(int appointmentId) {
        Appointment appointment = appointments.get(appointmentId);
        
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment not found");
        }
        
        if (appointment.getStatus() != Appointment.Status.BOOKED) {
            throw new IllegalStateException("Only booked appointments can be marked as attended");
        }
        
        appointment.markAsAttended();
    }
    
    // Getters for lists
    public List<Physiotherapist> getAllPhysiotherapists() {
        return new ArrayList<>(physiotherapists);
    }
    
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }
    
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }
    
    // Getter for a specific appointment
    public Appointment getAppointmentById(int id) {
        return appointments.get(id);
    }
}
