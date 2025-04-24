package main.java;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BoostPhysioApp {
    private BookingSystem bookingSystem;
    private ReportGenerator reportGenerator;
    private Scanner scanner;
    
    public BoostPhysioApp() {
        bookingSystem = new BookingSystem();
        reportGenerator = new ReportGenerator(bookingSystem);
        scanner = new Scanner(System.in);
        
        // Initialize with sample data
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Create physiotherapists
        Physiotherapist physio1 = new Physiotherapist(1, "John Smith", "123 Main St", "555-1234");
        physio1.addExpertiseArea("Physiotherapy");
        physio1.addExpertiseArea("Sports Therapy");
        
        Physiotherapist physio2 = new Physiotherapist(2, "Jane Doe", "456 Oak Ave", "555-5678");
        physio2.addExpertiseArea("Osteopathy");
        physio2.addExpertiseArea("Physiotherapy");
        
        Physiotherapist physio3 = new Physiotherapist(3, "Michael Johnson", "789 Pine Rd", "555-9012");
        physio3.addExpertiseArea("Acupuncture");
        physio3.addExpertiseArea("Massage Therapy");
        
        // Create treatments
        Treatment massage = new Treatment("Deep Tissue Massage", "Physiotherapy", 60);
        Treatment acupuncture = new Treatment("Acupuncture", "Acupuncture", 45);
        Treatment assessment = new Treatment("Initial Assessment", "Physiotherapy", 60);
        Treatment osteo = new Treatment("Osteopathic Treatment", "Osteopathy", 60);
        Treatment sports = new Treatment("Sports Rehab", "Sports Therapy", 45);
        
        // Assign treatments to physiotherapists
        physio1.addTreatment(massage);
        physio1.addTreatment(assessment);
        physio1.addTreatment(sports);
        
        physio2.addTreatment(assessment);
        physio2.addTreatment(osteo);
        
        physio3.addTreatment(acupuncture);
        physio3.addTreatment(massage);
        
        // Create time slots (for the next 4 weeks)
        LocalDate today = LocalDate.now();
        
        // Create time slots for each physiotherapist for 4 weeks
        for (int day = 0; day < 28; day++) {
            LocalDate currentDate = today.plusDays(day);
            String dateStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // Morning slots (9:00 to 12:00)
            for (int hour = 9; hour < 12; hour++) {
                LocalDateTime startTime = currentDate.atTime(hour, 0);
                LocalDateTime endTime = startTime.plusHours(1);
                
                physio1.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
                physio2.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
                physio3.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
            }
            
            // Afternoon slots (14:00 to 17:00)
            for (int hour = 14; hour < 17; hour++) {
                LocalDateTime startTime = currentDate.atTime(hour, 0);
                LocalDateTime endTime = startTime.plusHours(1);
                
                physio1.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
                physio2.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
                physio3.addTimeSlot(dateStr, new TimeSlot(startTime, endTime));
            }
        }
        
        // Add physiotherapists to system
        bookingSystem.addPhysiotherapist(physio1);
        bookingSystem.addPhysiotherapist(physio2);
        bookingSystem.addPhysiotherapist(physio3);
        
        // Create patients
        Patient patient1 = new Patient(101, "Alice Williams", "321 Elm St", "555-1111");
        Patient patient2 = new Patient(102, "Bob Johnson", "654 Maple Ave", "555-2222");
        Patient patient3 = new Patient(103, "Carol Davis", "987 Birch Rd", "555-3333");
        Patient patient4 = new Patient(104, "David Brown", "135 Cedar Ln", "555-4444");
        Patient patient5 = new Patient(105, "Emily Wilson", "246 Pine St", "555-5555");
        
        // Add patients to system
        bookingSystem.addPatient(patient1);
        bookingSystem.addPatient(patient2);
        bookingSystem.addPatient(patient3);
        bookingSystem.addPatient(patient4);
        bookingSystem.addPatient(patient5);
        
        // Create some sample appointments for demonstration
        String firstDay = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<TimeSlot> physio1Slots = physio1.getAvailableSlotsForDate(firstDay);
        
        if (!physio1Slots.isEmpty()) {
            TimeSlot slot = physio1Slots.get(0);
            bookingSystem.bookAppointment(patient1, physio1, massage, slot);
        }
        
        String secondDay = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<TimeSlot> physio2Slots = physio2.getAvailableSlotsForDate(secondDay);
        
        if (!physio2Slots.isEmpty()) {
            TimeSlot slot = physio2Slots.get(0);
            Appointment appt = bookingSystem.bookAppointment(patient2, physio2, osteo, slot);
            // Mark as attended for reporting demonstration
            appt.markAsAttended();
        }
    }
    
    public void start() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== BOOST PHYSIO CLINIC BOOKING SYSTEM =====");
            System.out.println("1. Patient Management");
            System.out.println("2. Book Appointment by Expertise Area");
            System.out.println("3. Book Appointment by Physiotherapist");
            System.out.println("4. Manage Appointments");
            System.out.println("5. Generate End of Term Report");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    managePatients();
                    break;
                case 2:
                    bookByExpertise();
                    break;
                case 3:
                    bookByPhysiotherapist();
                    break;
                case 4:
                    manageAppointments();
                    break;
                case 5:
                    generateReport();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private void managePatients() {
        boolean managing = true;
        
        while (managing) {
            System.out.println("\n----- PATIENT MANAGEMENT -----");
            System.out.println("1. List All Patients");
            System.out.println("2. Add New Patient");
            System.out.println("3. Remove Patient");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    listAllPatients();
                    break;
                    case 2:
                    addNewPatient();
                    break;
                case 3:
                    removePatient();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void listAllPatients() {
        System.out.println("\n----- ALL PATIENTS -----");
        List<Patient> patients = bookingSystem.getAllPatients();
        
        if (patients.isEmpty()) {
            System.out.println("No patients registered in the system.");
            return;
        }
        
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }
    
    private void addNewPatient() {
        System.out.println("\n----- ADD NEW PATIENT -----");
        
        System.out.print("Enter patient ID: ");
        int id = getIntInput();
        
        // Check if ID already exists
        if (bookingSystem.getPatientById(id) != null) {
            System.out.println("Error: A patient with this ID already exists.");
            return;
        }
        
        scanner.nextLine(); // Clear buffer
        
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        
        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();
        
        try {
            Patient newPatient = new Patient(id, fullName, address, phoneNumber);
            bookingSystem.addPatient(newPatient);
            System.out.println("Patient added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }
    
    private void removePatient() {
        System.out.println("\n----- REMOVE PATIENT -----");
        
        System.out.print("Enter patient ID to remove: ");
        int id = getIntInput();
        
        try {
            bookingSystem.removePatient(id);
            System.out.println("Patient removed successfully!");
        } catch (Exception e) {
            System.out.println("Error removing patient: " + e.getMessage());
        }
    }
    
    private void bookByExpertise() {
        System.out.println("\n----- BOOK APPOINTMENT BY EXPERTISE AREA -----");
        
        // List available expertise areas
        System.out.println("Available expertise areas:");
        
        // Get unique expertise areas from all physiotherapists
        List<String> expertiseAreas = bookingSystem.getAllPhysiotherapists().stream()
            .flatMap(p -> p.getExpertiseAreas().stream())
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        for (int i = 0; i < expertiseAreas.size(); i++) {
            System.out.println((i+1) + ". " + expertiseAreas.get(i));
        }
        
        System.out.print("Select expertise area (enter number): ");
        int areaChoice = getIntInput();
        
        if (areaChoice < 1 || areaChoice > expertiseAreas.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        String selectedExpertise = expertiseAreas.get(areaChoice - 1);
        
        // Get available slots for this expertise
        List<Map<String, Object>> availableSlots = 
            bookingSystem.searchAvailableSlotsByExpertise(selectedExpertise);
        
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for " + selectedExpertise);
            return;
        }
        
        // Show results
        System.out.println("\nAvailable appointments for " + selectedExpertise + ":");
        for (int i = 0; i < availableSlots.size(); i++) {
            Map<String, Object> slotInfo = availableSlots.get(i);
            Physiotherapist physio = (Physiotherapist) slotInfo.get("physiotherapist");
            Treatment treatment = (Treatment) slotInfo.get("treatment");
            TimeSlot slot = (TimeSlot) slotInfo.get("timeSlot");
            
            System.out.println((i+1) + ". " + physio.getFullName() + " - " + 
                             treatment.getName() + " - " + slot.getFormattedTimeRange());
        }
        
        // Proceed with booking
        System.out.print("\nSelect an appointment (enter number or 0 to cancel): ");
        int slotChoice = getIntInput();
        
        if (slotChoice < 1 || slotChoice > availableSlots.size()) {
            System.out.println("Booking cancelled.");
            return;
        }
        
        // Get selected slot info
        Map<String, Object> selectedSlotInfo = availableSlots.get(slotChoice - 1);
        Physiotherapist physio = (Physiotherapist) selectedSlotInfo.get("physiotherapist");
        Treatment treatment = (Treatment) selectedSlotInfo.get("treatment");
        TimeSlot slot = (TimeSlot) selectedSlotInfo.get("timeSlot");
        
        // Select patient
        System.out.println("\nSelect patient:");
        List<Patient> patients = bookingSystem.getAllPatients();
        
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i+1) + ". " + patients.get(i).getFullName());
        }
        
        System.out.print("Enter patient number: ");
        int patientChoice = getIntInput();
        
        if (patientChoice < 1 || patientChoice > patients.size()) {
            System.out.println("Invalid patient selection. Booking cancelled.");
            return;
        }
        
        Patient selectedPatient = patients.get(patientChoice - 1);
        
        // Create the appointment
        try {
            Appointment appointment = bookingSystem.bookAppointment(
                selectedPatient, physio, treatment, slot);
            
            System.out.println("\nAppointment booked successfully!");
            System.out.println("Appointment ID: " + appointment.getId());
            System.out.println(appointment);
        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }
    
    private void bookByPhysiotherapist() {
        System.out.println("\n----- BOOK APPOINTMENT BY PHYSIOTHERAPIST -----");
        
        // List available physiotherapists
        System.out.println("Available physiotherapists:");
        List<Physiotherapist> physiotherapists = bookingSystem.getAllPhysiotherapists();
        
        for (int i = 0; i < physiotherapists.size(); i++) {
            System.out.println((i+1) + ". " + physiotherapists.get(i).getFullName());
        }
        
        System.out.print("Select physiotherapist (enter number): ");
        int physioChoice = getIntInput();
        
        if (physioChoice < 1 || physioChoice > physiotherapists.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Physiotherapist selectedPhysio = physiotherapists.get(physioChoice - 1);
        
        // Get available slots for this physiotherapist
        List<Map<String, Object>> availableSlots = 
            bookingSystem.searchAvailableSlotsByPhysiotherapist(selectedPhysio.getFullName());
        
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for " + selectedPhysio.getFullName());
            return;
        }
        
        // Show treatments offered by this physiotherapist
        System.out.println("\nTreatments offered by " + selectedPhysio.getFullName() + ":");
        List<Treatment> treatments = selectedPhysio.getTreatments();
        
        for (int i = 0; i < treatments.size(); i++) {
            System.out.println((i+1) + ". " + treatments.get(i).getName() + 
                             " (" + treatments.get(i).getDurationMinutes() + " mins)");
        }
        
        System.out.print("Select treatment (enter number): ");
        int treatmentChoice = getIntInput();
        
        if (treatmentChoice < 1 || treatmentChoice > treatments.size()) {
            System.out.println("Invalid treatment selection. Booking cancelled.");
            return;
        }
        
        Treatment selectedTreatment = treatments.get(treatmentChoice - 1);
        
        // Filter slots by treatment
        List<Map<String, Object>> filteredSlots = availableSlots.stream()
            .filter(slot -> {
                String treatmentName = ((Treatment) slot.get("treatment")).getName();
                return treatmentName.equals(selectedTreatment.getName());
            })
            .collect(java.util.stream.Collectors.toList());
        
        if (filteredSlots.isEmpty()) {
            System.out.println("No available slots found for this treatment.");
            return;
        }
        
        // Show available time slots
        System.out.println("\nAvailable time slots for " + selectedTreatment.getName() + ":");
        
        for (int i = 0; i < filteredSlots.size(); i++) {
            Map<String, Object> slotInfo = filteredSlots.get(i);
            TimeSlot slot = (TimeSlot) slotInfo.get("timeSlot");
            
            System.out.println((i+1) + ". " + slot.getFormattedTimeRange());
        }
        
        System.out.print("\nSelect a time slot (enter number or 0 to cancel): ");
        int slotChoice = getIntInput();
        
        if (slotChoice < 1 || slotChoice > filteredSlots.size()) {
            System.out.println("Booking cancelled.");
            return;
        }
        
        // Get selected slot
        Map<String, Object> selectedSlotInfo = filteredSlots.get(slotChoice - 1);
        TimeSlot selectedSlot = (TimeSlot) selectedSlotInfo.get("timeSlot");
        
        // Select patient
        System.out.println("\nSelect patient:");
        List<Patient> patients = bookingSystem.getAllPatients();
        
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i+1) + ". " + patients.get(i).getFullName());
        }
        
        System.out.print("Enter patient number: ");
        int patientChoice = getIntInput();
        
        if (patientChoice < 1 || patientChoice > patients.size()) {
            System.out.println("Invalid patient selection. Booking cancelled.");
            return;
        }
        
        Patient selectedPatient = patients.get(patientChoice - 1);
        
        // Create the appointment
        try {
            Appointment appointment = bookingSystem.bookAppointment(
                selectedPatient, selectedPhysio, selectedTreatment, selectedSlot);
            
            System.out.println("\nAppointment booked successfully!");
            System.out.println("Appointment ID: " + appointment.getId());
            System.out.println(appointment);
        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }
    
    private void manageAppointments() {
        boolean managing = true;
        
        while (managing) {
            System.out.println("\n----- APPOINTMENT MANAGEMENT -----");
            System.out.println("1. List All Appointments");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. Reschedule Appointment");
            System.out.println("4. Mark Appointment as Attended");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    listAllAppointments();
                    break;
                case 2:
                    cancelAppointment();
                    break;
                case 3:
                    rescheduleAppointment();
                    break;
                case 4:
                    markAppointmentAsAttended();
                    break;
                case 0:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void listAllAppointments() {
        System.out.println("\n----- ALL APPOINTMENTS -----");
        List<Appointment> appointments = bookingSystem.getAllAppointments();
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments found in the system.");
            return;
        }
        
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }
    
    private void cancelAppointment() {
        System.out.println("\n----- CANCEL APPOINTMENT -----");
        System.out.print("Enter appointment ID to cancel: ");
        int id = getIntInput();
        
        try {
            bookingSystem.cancelAppointment(id);
            System.out.println("Appointment cancelled successfully!");
        } catch (Exception e) {
            System.out.println("Error cancelling appointment: " + e.getMessage());
        }
    }
    
    private void rescheduleAppointment() {
        System.out.println("\n----- RESCHEDULE APPOINTMENT -----");
        System.out.print("Enter appointment ID to reschedule: ");
        int id = getIntInput();
        
        Appointment appointment = bookingSystem.getAppointmentById(id);
        
        if (appointment == null) {
            System.out.println("Appointment not found.");
            return;
        }
        
        if (appointment.getStatus() != Appointment.Status.BOOKED) {
            System.out.println("Only booked appointments can be rescheduled.");
            return;
        }
        
        // Get the physiotherapist from the appointment
        Physiotherapist physio = appointment.getPhysiotherapist();
        
        // Show available time slots for this physiotherapist
        System.out.println("\nAvailable time slots for " + physio.getFullName() + ":");
        
        List<Map<String, Object>> availableSlots = 
            bookingSystem.searchAvailableSlotsByPhysiotherapist(physio.getFullName());
        
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for rescheduling.");
            return;
        }
        
        for (int i = 0; i < availableSlots.size(); i++) {
            Map<String, Object> slotInfo = availableSlots.get(i);
            TimeSlot slot = (TimeSlot) slotInfo.get("timeSlot");
            
            System.out.println((i+1) + ". " + slot.getFormattedTimeRange());
        }
        
        System.out.print("\nSelect a new time slot (enter number or 0 to cancel): ");
        int slotChoice = getIntInput();
        
        if (slotChoice < 1 || slotChoice > availableSlots.size()) {
            System.out.println("Rescheduling cancelled.");
            return;
        }
        
        // Get selected slot
        Map<String, Object> selectedSlotInfo = availableSlots.get(slotChoice - 1);
        TimeSlot selectedSlot = (TimeSlot) selectedSlotInfo.get("timeSlot");
        
        // Reschedule the appointment
        try {
            Appointment newAppointment = bookingSystem.rescheduleAppointment(id, selectedSlot);
            
            System.out.println("\nAppointment rescheduled successfully!");
            System.out.println("New Appointment ID: " + newAppointment.getId());
            System.out.println(newAppointment);
        } catch (Exception e) {
            System.out.println("Error rescheduling appointment: " + e.getMessage());
        }
    }
    
    private void markAppointmentAsAttended() {
        System.out.println("\n----- MARK APPOINTMENT AS ATTENDED -----");
        System.out.print("Enter appointment ID: ");
        int id = getIntInput();
        
        try {
            bookingSystem.markAppointmentAsAttended(id);
            System.out.println("Appointment marked as attended successfully!");
        } catch (Exception e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
    }
    
    private void generateReport() {
        System.out.println("\n----- GENERATE END OF TERM REPORT -----");
        String report = reportGenerator.generateEndOfTermReport();
        System.out.println("\n" + report);
    }
    
    private int getIntInput() {
        try {
            return scanner.nextInt();
        } catch (java.util.InputMismatchException e) {
            scanner.nextLine(); // Clear the invalid input
            return -1; // Return an invalid value
        }
    }
    
    public static void main(String[] args) {
        BoostPhysioApp app = new BoostPhysioApp();
        app.start();
    }
}
