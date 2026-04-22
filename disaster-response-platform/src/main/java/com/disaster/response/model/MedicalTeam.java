package com.disaster.response.model;

import jakarta.persistence.*;

/**
 * LSP: MedicalTeam IS-A ResponseTeam — can be used anywhere a ResponseTeam is expected.
 * Specialises in medical emergencies and trauma care.
 */
@Entity
@DiscriminatorValue("MEDICAL")
public class MedicalTeam extends ResponseTeam {

    private String specialization;   // e.g. "Trauma Care", "Burn Care"
    private Boolean hasAmbulance = false;
    private Integer numberOfDoctors = 0;

    public MedicalTeam() {
        super();
    }

    public MedicalTeam(String name, String location, Double latitude, Double longitude,
                       Integer capacity, String contactInfo, String description,
                       String specialization, Boolean hasAmbulance, Integer numberOfDoctors) {
        super(name, location, latitude, longitude, capacity, contactInfo, description);
        this.specialization = specialization;
        this.hasAmbulance = hasAmbulance;
        this.numberOfDoctors = numberOfDoctors;
    }

    @Override
    public String getSpecializationDescription() {
        return "Medical – " + (specialization != null ? specialization : "General") +
               (Boolean.TRUE.equals(hasAmbulance) ? " (Ambulance Available)" : "");
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.MEDICAL;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Boolean getHasAmbulance() { return hasAmbulance; }
    public void setHasAmbulance(Boolean hasAmbulance) { this.hasAmbulance = hasAmbulance; }

    public Integer getNumberOfDoctors() { return numberOfDoctors; }
    public void setNumberOfDoctors(Integer numberOfDoctors) { this.numberOfDoctors = numberOfDoctors; }
}
