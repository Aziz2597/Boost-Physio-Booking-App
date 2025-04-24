package main.java;


public class Treatment {
    private String name;
    private String expertiseArea;
    private int durationMinutes;
    
    public Treatment(String name, String expertiseArea, int durationMinutes) {
        this.name = name;
        this.expertiseArea = expertiseArea;
        this.durationMinutes = durationMinutes;
    }
    
    // Getters
    public String getName() { return name; }
    public String getExpertiseArea() { return expertiseArea; }
    public int getDurationMinutes() { return durationMinutes; }
    
    @Override
    public String toString() {
        return name + " (" + expertiseArea + ", " + durationMinutes + " mins)";
    }
}