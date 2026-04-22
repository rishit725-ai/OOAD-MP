package com.disaster.response.model;

import jakarta.persistence.*;

/**
 * LSP: SearchRescueTeam IS-A ResponseTeam — can be used anywhere a ResponseTeam is expected.
 * Specialises in locating and extracting survivors from disaster sites.
 */
@Entity
@DiscriminatorValue("SEARCH_RESCUE")
public class SearchRescueTeam extends ResponseTeam {

    private String searchCapability;   // e.g. "Urban", "Mountain", "Water"
    private Boolean hasDogs = false;
    private Boolean hasDrone = false;

    public SearchRescueTeam() {
        super();
    }

    public SearchRescueTeam(String name, String location, Double latitude, Double longitude,
                             Integer capacity, String contactInfo, String description,
                             String searchCapability, Boolean hasDogs, Boolean hasDrone) {
        super(name, location, latitude, longitude, capacity, contactInfo, description);
        this.searchCapability = searchCapability;
        this.hasDogs = hasDogs;
        this.hasDrone = hasDrone;
    }

    @Override
    public String getSpecializationDescription() {
        StringBuilder sb = new StringBuilder("Search & Rescue – ");
        sb.append(searchCapability != null ? searchCapability : "General");
        if (Boolean.TRUE.equals(hasDogs)) sb.append(" (K-9 Unit)");
        if (Boolean.TRUE.equals(hasDrone)) sb.append(" (Drone)");
        return sb.toString();
    }

    @Override
    public TeamType getTeamType() {
        return TeamType.SEARCH_RESCUE;
    }

    public String getSearchCapability() { return searchCapability; }
    public void setSearchCapability(String searchCapability) { this.searchCapability = searchCapability; }

    public Boolean getHasDogs() { return hasDogs; }
    public void setHasDogs(Boolean hasDogs) { this.hasDogs = hasDogs; }

    public Boolean getHasDrone() { return hasDrone; }
    public void setHasDrone(Boolean hasDrone) { this.hasDrone = hasDrone; }
}
