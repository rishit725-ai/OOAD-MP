package com.disaster.response.model;

/**
 * Shelter operational states — drives State Diagram 3.
 */
public enum ShelterStatus {
    OPEN("Open", "success"),
    FULL("Full", "danger"),
    STANDBY("Standby", "warning"),
    CLOSED("Closed", "dark");

    private final String displayName;
    private final String bootstrapColor;

    ShelterStatus(String displayName, String bootstrapColor) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
}
