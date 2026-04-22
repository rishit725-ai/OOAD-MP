package com.disaster.response.model;

import jakarta.persistence.*;

/**
 * LSP: FireTeam IS-A ResponseTeam — can be used anywhere a ResponseTeam is expected.
 * Specialises in fire suppression and hazardous material incidents.
 */
@Entity
@DiscriminatorValue("FIRE")
public class FireTeam extends ResponseTeam {

    private String equipmentType;          // e.g. "Class A", "Class B/C"
    private Integer waterTankCapacity;     // litres
    private Boolean hasHazmatSuit = false;

    public FireTeam() {
        super();
    }

    public FireTeam(String name, String location, Double latitude, Double longitude,
                    Integer capacity, String contactInfo, String description,
                    String equipmentType, Integer waterTankCapacity, Boolean hasHazmatSuit) {
        super(name, location, latitude, longitude, capacity, contactInfo, description);
        this.equipmentType = equipmentType;
        this.waterTankCapacity = waterTankCapacity;
        this.hasHazmatSuit = hasHazmatSuit;
    }

    @Override
    public String getSpecializationDescription() {
        return "Fire – " + (equipmentType != null ? equipmentType : "Standard") +
               (Boolean.TRUE.equals(hasHazmatSuit) ? " (HazMat Equipped)" : "");
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.FIRE;
    }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public Integer getWaterTankCapacity() { return waterTankCapacity; }
    public void setWaterTankCapacity(Integer waterTankCapacity) { this.waterTankCapacity = waterTankCapacity; }

    public Boolean getHasHazmatSuit() { return hasHazmatSuit; }
    public void setHasHazmatSuit(Boolean hasHazmatSuit) { this.hasHazmatSuit = hasHazmatSuit; }
}
