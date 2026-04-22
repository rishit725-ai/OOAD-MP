package com.disaster.response.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all response team types.
 *
 * LSP: MedicalTeam, FireTeam, SearchRescueTeam all extend this and can be
 *      substituted wherever ResponseTeam is expected without breaking behaviour.
 *
 * SINGLE_TABLE inheritance keeps all team data in one table for simple querying.
 * The factory creates the correct concrete subclass based on TeamType.
 */
@Entity
@Table(name = "response_teams")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "team_dtype", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class ResponseTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    private String location;

    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private TeamStatus status = TeamStatus.AVAILABLE;

    @Column(nullable = false)
    private Integer capacity = 10;

    @Column(nullable = false)
    private Integer currentLoad = 0;

    private String contactInfo;
    private String description;

    @OneToMany(mappedBy = "assignedTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignedTeam", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Equipment> equipment = new ArrayList<>();

    // ===== Constructors =====
    public ResponseTeam() {}

    public ResponseTeam(String name, String location, Double latitude, Double longitude,
                        Integer capacity, String contactInfo, String description) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.status = TeamStatus.AVAILABLE;
        this.contactInfo = contactInfo;
        this.description = description;
    }

    // ===== Abstract method — subclasses must implement (OCP + LSP) =====
    public abstract String getSpecializationDescription();

    public abstract TeamType getTeamType();

    // ===== Common business methods =====
    public int getAvailableCapacity() {
        return capacity - currentLoad;
    }

    public boolean isAvailable() {
        return status == TeamStatus.AVAILABLE && getAvailableCapacity() > 0;
    }

    /** Haversine distance from this team to a given lat/lon (in km). */
    public double distanceTo(double targetLat, double targetLon) {
        if (latitude == null || longitude == null) return Double.MAX_VALUE;
        final double R = 6371.0;
        double dLat = Math.toRadians(targetLat - latitude);
        double dLon = Math.toRadians(targetLon - longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

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

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public List<Equipment> getEquipment() { return equipment; }
    public void setEquipment(List<Equipment> equipment) { this.equipment = equipment; }
}
