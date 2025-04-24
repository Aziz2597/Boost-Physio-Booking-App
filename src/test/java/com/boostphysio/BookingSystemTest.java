package com.boostphysio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class BookingSystemTest {
    private BookingSystem bookingSystem;
    private Physiotherapist testPhysio;
    private Patient testPatient;
    private Treatment testTreatment;
    private TimeSlot testTimeSlot;

    @BeforeEach
    void setUp() {
        bookingSystem = new BookingSystem();

        // Create test data
        testPhysio = new Physiotherapist(1, "Test Physio", "123 Test St", "555-TEST");
        testPhysio.addExpertiseArea("Physiotherapy");

        testTreatment = new Treatment("Test Treatment", "Physiotherapy", 60);
        testPhysio.addTreatment(testTreatment);

        LocalDateTime now = LocalDateTime.now();
        testTimeSlot = new TimeSlot(now, now.plusHours(1));
        testPhysio.addTimeSlot("2025-05-01", testTimeSlot);

        testPatient = new Patient(101, "Test Patient", "456 Test Ave", "555-PATIENT");

        // Add to system
        bookingSystem.addPhysiotherapist(testPhysio);
        bookingSystem.addPatient(testPatient);
    }

    @Test
    void testAddAndGetPatient() {
        System.out.println("\n--- Test: Add and Retrieve Patient ---");
        Patient newPatient = new Patient(102, "New Patient", "789 New St", "555-NEW");
        System.out.println("Adding patient: ID=102, Name=New Patient");
        bookingSystem.addPatient(newPatient);

        System.out.println("Retrieving patient with ID=102...");
        Patient retrieved = bookingSystem.getPatientById(102);
        assertNotNull(retrieved, "Patient should exist in the system");
        assertEquals("New Patient", retrieved.getFullName(), "Patient name should match");
        System.out.println("SUCCESS: Patient retrieved and validated.");
    }

    @Test
    void testAddDuplicatePatient() {
        System.out.println("\n--- Test: Add Duplicate Patient ID ---");
        Patient duplicate = new Patient(101, "Duplicate Patient", "Same ID", "555-DUP");
        System.out.println("Attempting to add duplicate patient with ID=101...");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.addPatient(duplicate);
        });

        assertTrue(exception.getMessage().contains("already exists"),
                "Error message should indicate duplicate ID");
        System.out.println("SUCCESS: Duplicate patient addition blocked.");
    }

    @Test
    void testRemovePatient() {
        System.out.println("\n--- Test: Remove Patient ---");
        System.out.println("Removing patient with ID=101...");
        bookingSystem.removePatient(101);
        assertNull(bookingSystem.getPatientById(101), "Patient should no longer exist");
        System.out.println("SUCCESS: Patient removed.");
    }

    @Test
    void testRemovePatientWithActiveAppointments() {
        System.out.println("\n--- Test: Remove Patient with Active Appointments ---");
        System.out.println("Booking an appointment for patient ID=101...");
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        assertNotNull(appointment, "Appointment should be booked");

        System.out.println("Attempting to remove patient with active appointments...");
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookingSystem.removePatient(101);
        });

        assertTrue(exception.getMessage().contains("active appointments"),
                "Error message should block removal");
        System.out.println("SUCCESS: Patient removal blocked due to active appointments.");
    }

    @Test
    void testBookAppointment() {
        System.out.println("\n--- Test: Book Appointment ---");
        System.out.println("Booking appointment for patient ID=101...");
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);

        assertNotNull(appointment, "Appointment should be created");
        assertEquals(testPatient, appointment.getPatient(), "Patient should match");
        assertEquals(testPhysio, appointment.getPhysiotherapist(), "Physiotherapist should match");
        assertEquals(Appointment.Status.BOOKED, appointment.getStatus(), "Status should be BOOKED");
        assertFalse(testTimeSlot.isAvailable(), "Time slot should be marked as unavailable");
        System.out.println("SUCCESS: Appointment booked and time slot updated.");
    }

    @Test
    void testCancelAppointment() {
        System.out.println("\n--- Test: Cancel Appointment ---");
        System.out.println("Booking an appointment to cancel...");
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();

        System.out.println("Cancelling appointment ID=" + appointmentId + "...");
        bookingSystem.cancelAppointment(appointmentId);

        Appointment canceled = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.CANCELLED, canceled.getStatus(), "Status should be CANCELLED");
        assertTrue(testTimeSlot.isAvailable(), "Time slot should be available again");
        System.out.println("SUCCESS: Appointment cancelled and time slot freed.");
    }

    @Test
    void testRescheduleAppointment() {
        System.out.println("\n--- Test: Reschedule Appointment ---");
        System.out.println("Booking initial appointment...");
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();

        System.out.println("Creating a new time slot for rescheduling...");
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        TimeSlot newSlot = new TimeSlot(tomorrow, tomorrow.plusHours(1));
        testPhysio.addTimeSlot("2025-05-02", newSlot);

        System.out.println("Rescheduling appointment to new slot...");
        Appointment rescheduled = bookingSystem.rescheduleAppointment(appointmentId, newSlot);

        // Check original appointment is canceled
        Appointment original = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.CANCELLED, original.getStatus(), "Original appointment should be CANCELLED");
        assertTrue(testTimeSlot.isAvailable(), "Original time slot should be available");

        // Check new appointment
        assertNotNull(rescheduled, "New appointment should exist");
        assertEquals(newSlot, rescheduled.getTimeSlot(), "Time slot should be updated");
        assertFalse(newSlot.isAvailable(), "New time slot should be booked");
        System.out.println("SUCCESS: Appointment rescheduled.");
    }

    @Test
    void testSearchByExpertise() {
        System.out.println("\n--- Test: Search by Expertise ---");
        System.out.println("Adding a second physiotherapist with Osteopathy expertise...");
        Physiotherapist physio2 = new Physiotherapist(2, "Other Physio", "Other St", "555-OTHER");
        physio2.addExpertiseArea("Osteopathy");
        Treatment osteo = new Treatment("Osteo Treatment", "Osteopathy", 45);
        physio2.addTreatment(osteo);
        TimeSlot otherSlot = new TimeSlot(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1));
        physio2.addTimeSlot("2025-05-02", otherSlot);
        bookingSystem.addPhysiotherapist(physio2);

        System.out.println("Searching for Physiotherapy experts...");
        List<Map<String, Object>> physioResults = bookingSystem.searchAvailableSlotsByExpertise("Physiotherapy");
        assertFalse(physioResults.isEmpty(), "Results should not be empty");
        assertEquals(testPhysio, physioResults.get(0).get("physiotherapist"), "First result should be Test Physio");

        System.out.println("Searching for Osteopathy experts...");
        List<Map<String, Object>> osteoResults = bookingSystem.searchAvailableSlotsByExpertise("Osteopathy");
        assertFalse(osteoResults.isEmpty(), "Results should not be empty");
        assertEquals(physio2, osteoResults.get(0).get("physiotherapist"), "First result should be Other Physio");
        System.out.println("SUCCESS: Expertise search validated.");
    }

    @Test
    void testSearchByPhysiotherapist() {
        System.out.println("\n--- Test: Search by Physiotherapist Name ---");
        System.out.println("Searching for 'Test Physio'...");
        List<Map<String, Object>> results = bookingSystem.searchAvailableSlotsByPhysiotherapist("Test Physio");

        assertFalse(results.isEmpty(), "Results should not be empty");
        assertEquals(testPhysio, results.get(0).get("physiotherapist"), "Physiotherapist should match");
        assertEquals(testTimeSlot, results.get(0).get("timeSlot"), "Time slot should match");
        System.out.println("SUCCESS: Physiotherapist search validated.");
    }

    @Test
    void testMarkAppointmentAsAttended() {
        System.out.println("\n--- Test: Mark Appointment as Attended ---");
        System.out.println("Booking an appointment...");
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();

        System.out.println("Marking appointment ID=" + appointmentId + " as attended...");
        bookingSystem.markAppointmentAsAttended(appointmentId);

        Appointment attended = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.ATTENDED, attended.getStatus(), "Status should be ATTENDED");
        System.out.println("SUCCESS: Appointment marked as attended.");
    }
}