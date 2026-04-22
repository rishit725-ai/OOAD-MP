package com.disaster.response.model;

/**
 * Incident severity levels used in Strategy pattern for task assignment priority.
 */
public enum Severity {
    CRITICAL("Critical", "danger", 4),
    HIGH("High", "warning", 3),
    MEDIUM("Medium", "info", 2),
    LOW("Low", "success", 1);

    private final String displayName;
    private final String bootstrapColor;
    private final int priorityLevel;

    Severity(String displayName, String bootstrapColor, int priorityLevel) {
        this.displayName = displayName;
        this.bootstrapColor = bootstrapColor;
        this.priorityLevel = priorityLevel;
    }

    public String getDisplayName() { return displayName; }
    public String getBootstrapColor() { return bootstrapColor; }
    public int getPriorityLevel() { return priorityLevel; }
}
