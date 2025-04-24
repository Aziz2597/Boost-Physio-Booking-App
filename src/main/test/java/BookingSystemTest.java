package main.test.java;

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
        Patient newPatient = new Patient(102, "New Patient", "789 New St", "555-NEW");
        bookingSystem.addPatient(newPatient);
        
        Patient retrieved = bookingSystem.getPatientById(102);
        assertNotNull(retrieved);
        assertEquals("New Patient", retrieved.getFullName());
    }
    
    @Test
    void testAddDuplicatePatient() {
        Patient duplicate = new Patient(101, "Duplicate Patient", "Same ID", "555-DUP");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingSystem.addPatient(duplicate);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
    }
    
    @Test
    void testRemovePatient() {
        bookingSystem.removePatient(101);
        assertNull(bookingSystem.getPatientById(101));
    }
    
    @Test
    void testRemovePatientWithActiveAppointments() {
        // Book an appointment for the test patient
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        assertNotNull(appointment);
        
        // Try to remove the patient
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookingSystem.removePatient(101);
        });
        
        assertTrue(exception.getMessage().contains("active appointments"));
    }
    
    @Test
    void testBookAppointment() {
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        
        assertNotNull(appointment);
        assertEquals(testPatient, appointment.getPatient());
        assertEquals(testPhysio, appointment.getPhysiotherapist());
        assertEquals(testTreatment, appointment.getTreatment());
        assertEquals(testTimeSlot, appointment.getTimeSlot());
        assertEquals(Appointment.Status.BOOKED, appointment.getStatus());
        
        // Time slot should now be unavailable
        assertFalse(testTimeSlot.isAvailable());
    }
    
    @Test
    void testCancelAppointment() {
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();
        
        bookingSystem.cancelAppointment(appointmentId);
        
        Appointment canceled = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.CANCELLED, canceled.getStatus());
        
        // Time slot should be available again
        assertTrue(testTimeSlot.isAvailable());
    }
    
    @Test
    void testRescheduleAppointment() {
        // Book initial appointment
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();
        
        // Create a new time slot for rescheduling
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        TimeSlot newSlot = new TimeSlot(tomorrow, tomorrow.plusHours(1));
        testPhysio.addTimeSlot("2025-05-02", newSlot);
        
        // Reschedule
        Appointment rescheduled = bookingSystem.rescheduleAppointment(appointmentId, newSlot);
        
        // Check original appointment is canceled
        Appointment original = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.CANCELLED, original.getStatus());
        
        // Check new appointment is booked
        assertNotNull(rescheduled);
        assertEquals(testPatient, rescheduled.getPatient());
        assertEquals(testPhysio, rescheduled.getPhysiotherapist());
        assertEquals(testTreatment, rescheduled.getTreatment());
        assertEquals(newSlot, rescheduled.getTimeSlot());
        assertEquals(Appointment.Status.BOOKED, rescheduled.getStatus());
        
        // Original slot should be available again
        assertTrue(testTimeSlot.isAvailable());
        
        // New slot should be unavailable
        assertFalse(newSlot.isAvailable());
    }
    
    @Test
    void testSearchByExpertise() {
        // Add another physiotherapist with different expertise
        Physiotherapist physio2 = new Physiotherapist(2, "Other Physio", "Other St", "555-OTHER");
        physio2.addExpertiseArea("Osteopathy");
        Treatment osteo = new Treatment("Osteo Treatment", "Osteopathy", 45);
        physio2.addTreatment(osteo);
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        TimeSlot otherSlot = new TimeSlot(tomorrow, tomorrow.plusHours(1));
        physio2.addTimeSlot("2025-05-02", otherSlot);
        
        bookingSystem.addPhysiotherapist(physio2);
        
        // Search for Physiotherapy experts
        List<Map<String, Object>> physioResults = bookingSystem.searchAvailableSlotsByExpertise("Physiotherapy");
        assertFalse(physioResults.isEmpty());
        
        // First result should contain our test physio
        Map<String, Object> firstResult = physioResults.get(0);
        assertEquals(testPhysio, firstResult.get("physiotherapist"));
        
        // Search for Osteopathy experts
        List<Map<String, Object>> osteoResults = bookingSystem.searchAvailableSlotsByExpertise("Osteopathy");
        assertFalse(osteoResults.isEmpty());
        
        // First result should contain our second physio
        Map<String, Object> osteoResult = osteoResults.get(0);
        assertEquals(physio2, osteoResult.get("physiotherapist"));
    }
    
    @Test
    void testSearchByPhysiotherapist() {
        List<Map<String, Object>> results = bookingSystem.searchAvailableSlotsByPhysiotherapist("Test Physio");
        
        assertFalse(results.isEmpty());
        
        // Results should contain our physio and the test time slot
        Map<String, Object> firstResult = results.get(0);
        assertEquals(testPhysio, firstResult.get("physiotherapist"));
        assertEquals(testTimeSlot, firstResult.get("timeSlot"));
    }
    
    @Test
    void testMarkAppointmentAsAttended() {
        Appointment appointment = bookingSystem.bookAppointment(testPatient, testPhysio, testTreatment, testTimeSlot);
        int appointmentId = appointment.getId();
        
        bookingSystem.markAppointmentAsAttended(appointmentId);
        
        Appointment attended = bookingSystem.getAppointmentById(appointmentId);
        assertEquals(Appointment.Status.ATTENDED, attended.getStatus());
    }
}
