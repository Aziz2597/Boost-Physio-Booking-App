package com.boostphysio;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    private BookingSystem bookingSystem;
    
    public ReportGenerator(BookingSystem bookingSystem) {
        this.bookingSystem = bookingSystem;
    }
    
    public String generateEndOfTermReport() {
        StringBuilder report = new StringBuilder();
        report.append("===== BOOST PHYSIO CLINIC: END OF TERM REPORT =====\n\n");
        
        // Get all physiotherapists
        List<Physiotherapist> allPhysios = bookingSystem.getAllPhysiotherapists();
        List<Appointment> allAppointments = bookingSystem.getAllAppointments();
        
        // Group appointments by physiotherapist
        Map<Physiotherapist, List<Appointment>> appointmentsByPhysio = allAppointments.stream()
            .collect(Collectors.groupingBy(Appointment::getPhysiotherapist));
        
        // Count attended appointments per physiotherapist
        Map<Physiotherapist, Long> attendedCountByPhysio = new HashMap<>();
        
        for (Physiotherapist physio : allPhysios) {
            List<Appointment> physioAppointments = appointmentsByPhysio.getOrDefault(physio, List.of());
            
            long attendedCount = physioAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.ATTENDED)
                .count();
            
            attendedCountByPhysio.put(physio, attendedCount);
        }
        
        // Sort physiotherapists by attended appointments (descending)
        List<Physiotherapist> sortedPhysios = allPhysios.stream()
            .sorted(Comparator.comparing(p -> attendedCountByPhysio.getOrDefault(p, 0L), Comparator.reverseOrder()))
            .collect(Collectors.toList());
        
        // Build the report
        report.append("PHYSIOTHERAPIST RANKINGS (by attended appointments):\n");
        report.append("------------------------------------------------\n");
        
        for (int i = 0; i < sortedPhysios.size(); i++) {
            Physiotherapist physio = sortedPhysios.get(i);
            long attendedCount = attendedCountByPhysio.getOrDefault(physio, 0L);
            
            report.append(String.format("%d. %s - %d attended appointments\n", 
                                       i+1, physio.getFullName(), attendedCount));
        }
        
        report.append("\n\nDETAILED APPOINTMENT RECORDS BY PHYSIOTHERAPIST:\n");
        report.append("------------------------------------------------\n\n");
        
        // Add detailed appointment records for each physiotherapist
        for (Physiotherapist physio : sortedPhysios) {
            report.append("PHYSIOTHERAPIST: ").append(physio.getFullName()).append("\n");
            report.append("Expertise Areas: ").append(String.join(", ", physio.getExpertiseAreas())).append("\n");
            report.append("--------------------------------------------------\n");
            
            List<Appointment> physioAppointments = appointmentsByPhysio.getOrDefault(physio, List.of());
            
            if (physioAppointments.isEmpty()) {
                report.append("No appointments recorded.\n\n");
                continue;
            }
            
            // Count statistics
            long booked = physioAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.BOOKED)
                .count();
            
            long cancelled = physioAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.CANCELLED)
                .count();
            
            long attended = physioAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.ATTENDED)
                .count();
            
            report.append(String.format("Total Appointments: %d (Booked: %d, Cancelled: %d, Attended: %d)\n\n", 
                          physioAppointments.size(), booked, cancelled, attended));
            
            report.append("APPOINTMENT DETAILS:\n");
            for (Appointment appt : physioAppointments) {
                report.append(String.format("- %s | Treatment: %s | Patient: %s | Status: %s\n",
                                          appt.getTimeSlot().getFormattedTimeRange(),
                                          appt.getTreatment().getName(),
                                          appt.getPatient().getFullName(),
                                          appt.getStatus()));
            }
            
            report.append("\n\n");
        }
        
        return report.toString();
    }
}