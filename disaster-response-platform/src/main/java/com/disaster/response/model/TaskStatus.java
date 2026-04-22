package com.disaster.response.model;

/**
 * Task lifecycle states.
 */
public enum TaskStatus {
    PENDING("Pending", "secondary"),
    ASSIGNED("Assigned", "primary"),
    IN_PROGRESS("In Progress", "warning"),
    COMPLETED("Completed", "success"),
    CANCELLED("Cancelled", "dark");

    private final String displayName;
    private final String bootstrapColor;

    TaskStatus(String displayName, String bootstrapColor) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
}
