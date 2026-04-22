package com.disaster.response.model;

/**
 * Lifecycle states for an Incident — drives State Diagram 1.
 */
public enum IncidentStatus {
    REPORTED("Reported", "secondary"),
    ASSIGNED("Assigned", "primary"),
    IN_PROGRESS("In Progress", "warning"),
    RESOLVED("Resolved", "success"),
    CLOSED("Closed", "dark");

    private final String displayName;
    private final String bootstrapColor;

    IncidentStatus(String displayName, String bootstrapColor) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
}
