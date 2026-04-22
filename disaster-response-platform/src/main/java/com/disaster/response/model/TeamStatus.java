package com.disaster.response.model;

/**
 * Operational states for a ResponseTeam — drives State Diagram 2.
 */
public enum TeamStatus {
    AVAILABLE("Available", "success"),
    DEPLOYED("Deployed", "warning"),
    STANDBY("Standby", "info"),
    UNAVAILABLE("Unavailable", "danger");

    private final String displayName;
    private final String bootstrapColor;

    TeamStatus(String displayName, String bootstrapColor) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
}
