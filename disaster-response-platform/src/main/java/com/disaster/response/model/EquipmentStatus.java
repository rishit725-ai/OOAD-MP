package com.disaster.response.model;

/**
 * Equipment lifecycle states — drives State Diagram 4.
 */
public enum EquipmentStatus {
    AVAILABLE("Available", "success"),
    IN_USE("In Use", "warning"),
    MAINTENANCE("Maintenance", "info"),
    DAMAGED("Damaged", "danger");

    private final String displayName;
    private final String bootstrapColor;

    EquipmentStatus(String displayName, String bootstrapColor) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
}
