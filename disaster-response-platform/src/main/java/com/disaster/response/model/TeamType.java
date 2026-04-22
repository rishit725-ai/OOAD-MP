package com.disaster.response.model;

/**
 * Discriminator for ResponseTeam hierarchy — used by Factory and LSP subclasses.
 */
public enum TeamType {
    MEDICAL("Medical Team", "heart-pulse"),
    FIRE("Fire Brigade", "fire"),
    SEARCH_RESCUE("Search & Rescue", "search"),
    POLICE("Police Unit", "shield");

    private final String displayName;
    private final String icon;

    TeamType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
}
