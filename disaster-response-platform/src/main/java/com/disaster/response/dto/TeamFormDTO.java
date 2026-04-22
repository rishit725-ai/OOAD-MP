package com.disaster.response.dto;

import com.disaster.response.model.TeamStatus;
import com.disaster.response.model.TeamType;

/**
 * Data Transfer Object for the team create/edit form.
 * DRY: One form object captures all team types; controller uses Factory to create the right subclass.
 * Decouples Thymeleaf form binding from JPA entity hierarchy.
 */
public class TeamFormDTO {

    // Common fields
    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private TeamStatus status = TeamStatus.AVAILABLE;
    private Integer capacity = 10;
    private Integer currentLoad = 0;
    private String contactInfo;
    private String description;
    private TeamType teamType = TeamType.MEDICAL;

    // MedicalTeam fields
    private String specialization;
    private Boolean hasAmbulance = false;
    private Integer numberOfDoctors = 0;

    // FireTeam fields
    private String equipmentType;
    private Integer waterTankCapacity = 0;
    private Boolean hasHazmatSuit = false;

    // SearchRescueTeam fields
    private String searchCapability;
    private Boolean hasDogs = false;
    private Boolean hasDrone = false;

    // PoliceTeam fields
    private String jurisdictionArea;
    private Integer numberOfOfficers = 0;
    private Boolean hasArmoredVehicle = false;

    public TeamFormDTO() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public TeamStatus getStatus() { return status; }
    public void setStatus(TeamStatus status) { this.status = status; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TeamType getTeamType() { return teamType; }
    public void setTeamType(TeamType teamType) { this.teamType = teamType; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Boolean getHasAmbulance() { return hasAmbulance; }
    public void setHasAmbulance(Boolean hasAmbulance) { this.hasAmbulance = hasAmbulance; }

    public Integer getNumberOfDoctors() { return numberOfDoctors; }
    public void setNumberOfDoctors(Integer numberOfDoctors) { this.numberOfDoctors = numberOfDoctors; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public Integer getWaterTankCapacity() { return waterTankCapacity; }
    public void setWaterTankCapacity(Integer waterTankCapacity) { this.waterTankCapacity = waterTankCapacity; }

    public Boolean getHasHazmatSuit() { return hasHazmatSuit; }
    public void setHasHazmatSuit(Boolean hasHazmatSuit) { this.hasHazmatSuit = hasHazmatSuit; }

    public String getSearchCapability() { return searchCapability; }
    public void setSearchCapability(String searchCapability) { this.searchCapability = searchCapability; }

    public Boolean getHasDogs() { return hasDogs; }
    public void setHasDogs(Boolean hasDogs) { this.hasDogs = hasDogs; }

    public Boolean getHasDrone() { return hasDrone; }
    public void setHasDrone(Boolean hasDrone) { this.hasDrone = hasDrone; }

    public String getJurisdictionArea() { return jurisdictionArea; }
    public void setJurisdictionArea(String jurisdictionArea) { this.jurisdictionArea = jurisdictionArea; }

    public Integer getNumberOfOfficers() { return numberOfOfficers; }
    public void setNumberOfOfficers(Integer numberOfOfficers) { this.numberOfOfficers = numberOfOfficers; }

    public Boolean getHasArmoredVehicle() { return hasArmoredVehicle; }
    public void setHasArmoredVehicle(Boolean hasArmoredVehicle) { this.hasArmoredVehicle = hasArmoredVehicle; }
}
