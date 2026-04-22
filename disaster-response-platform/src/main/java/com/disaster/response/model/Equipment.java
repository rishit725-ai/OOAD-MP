package com.disaster.response.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * SRP: Equipment entity tracks resource details only.
 * EquipmentService handles allocation and status changes.
 */
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String category;    // e.g. "Firefighting", "Medical", "Communication"
    private String model;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    private Integer quantity = 1;
    private Integer availableQuantity = 1;

    private String storageLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_team_id")
    private ResponseTeam assignedTeam;

    private String description;

    // ===== Constructors =====
    public Equipment() {}

    public Equipment(String name, String category, String model, Integer quantity, String storageLocation, String description) {
        this.name = name;
        this.category = category;
        this.model = model;
        this.quantity = quantity;
        this.availableQuantity = quantity;
        this.storageLocation = storageLocation;
        this.description = description;
        this.status = EquipmentStatus.AVAILABLE;
    }

    // ===== Business method =====
    public String getAssignedTeamName() {
        return assignedTeam != null ? assignedTeam.getName() : "Unassigned";
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public ResponseTeam getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(ResponseTeam assignedTeam) { this.assignedTeam = assignedTeam; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
