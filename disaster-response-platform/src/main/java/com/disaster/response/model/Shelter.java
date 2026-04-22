package com.disaster.response.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * SRP: Shelter is solely responsible for shelter data — capacity, occupancy, amenities.
 * ShelterService handles business logic.
 */
@Entity
@Table(name = "shelters")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String location;

    private Double latitude;
    private Double longitude;

    @NotNull
    private Integer capacity;

    private Integer currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    private ShelterStatus status = ShelterStatus.STANDBY;

    private String contactPerson;
    private String contactPhone;

    private Boolean hasFood = false;
    private Boolean hasMedical = false;
    private Boolean hasPower = false;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    // ===== Constructors =====
    public Shelter() {}

    public Shelter(String name, String location, Double latitude, Double longitude,
                   Integer capacity, String contactPerson, String contactPhone,
                   Boolean hasFood, Boolean hasMedical, Boolean hasPower) {
        this.name = name;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capacity = capacity;
        this.currentOccupancy = 0;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.hasFood = hasFood;
        this.hasMedical = hasMedical;
        this.hasPower = hasPower;
        this.status = ShelterStatus.STANDBY;
    }

    // ===== Business methods =====
    public int getAvailableSpots() {
        return capacity - currentOccupancy;
    }

    public int getOccupancyPercentage() {
        if (capacity == 0) return 0;
        return (int) ((currentOccupancy * 100.0) / capacity);
    }

    public boolean isFull() {
        return currentOccupancy >= capacity;
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

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public ShelterStatus getStatus() { return status; }
    public void setStatus(ShelterStatus status) { this.status = status; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Boolean getHasFood() { return hasFood; }
    public void setHasFood(Boolean hasFood) { this.hasFood = hasFood; }

    public Boolean getHasMedical() { return hasMedical; }
    public void setHasMedical(Boolean hasMedical) { this.hasMedical = hasMedical; }

    public Boolean getHasPower() { return hasPower; }
    public void setHasPower(Boolean hasPower) { this.hasPower = hasPower; }

    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
