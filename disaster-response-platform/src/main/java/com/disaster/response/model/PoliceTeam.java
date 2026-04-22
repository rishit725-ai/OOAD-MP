package com.disaster.response.model;

import jakarta.persistence.*;

/**
 * LSP: PoliceTeam IS-A ResponseTeam — handles crowd control and area security.
 */
@Entity
@DiscriminatorValue("POLICE")
public class PoliceTeam extends ResponseTeam {

    private String jurisdictionArea;
    private Integer numberOfOfficers = 0;
    private Boolean hasArmoredVehicle = false;

    public PoliceTeam() {
        super();
    }

    public PoliceTeam(String name, String location, Double latitude, Double longitude,
                      Integer capacity, String contactInfo, String description,
                      String jurisdictionArea, Integer numberOfOfficers, Boolean hasArmoredVehicle) {
        super(name, location, latitude, longitude, capacity, contactInfo, description);
        this.jurisdictionArea = jurisdictionArea;
        this.numberOfOfficers = numberOfOfficers;
        this.hasArmoredVehicle = hasArmoredVehicle;
    }

    @Override
    public String getSpecializationDescription() {
        return "Police – " + (jurisdictionArea != null ? jurisdictionArea : "General") +
               " (" + numberOfOfficers + " officers)";
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.POLICE;
    }

    public String getJurisdictionArea() { return jurisdictionArea; }
    public void setJurisdictionArea(String jurisdictionArea) { this.jurisdictionArea = jurisdictionArea; }

    public Integer getNumberOfOfficers() { return numberOfOfficers; }
    public void setNumberOfOfficers(Integer numberOfOfficers) { this.numberOfOfficers = numberOfOfficers; }

    public Boolean getHasArmoredVehicle() { return hasArmoredVehicle; }
    public void setHasArmoredVehicle(Boolean hasArmoredVehicle) { this.hasArmoredVehicle = hasArmoredVehicle; }
}
