package com.disaster.response.factory;

import com.disaster.response.dto.TeamFormDTO;
import com.disaster.response.model.*;
import org.springframework.stereotype.Component;

/**
 * FACTORY PATTERN — Creational Pattern
 *
 * Centralises the creation of ResponseTeam subclass instances.
 * Callers (controllers, services) ask for a ResponseTeam without knowing which concrete
 * subclass is being created — they only specify the TeamType.
 *
 * OCP: Adding a new team type (e.g., PoliceTeam) requires only adding a new case here.
 * SRP: Only responsible for object creation, not business logic.
 */
@Component
public class ResponseTeamFactory {

    /**
     * Creates the appropriate ResponseTeam subclass from a form DTO.
     *
     * @param dto form data from the UI
     * @return concrete ResponseTeam subclass (MedicalTeam, FireTeam, SearchRescueTeam, or PoliceTeam)
     */
    public ResponseTeam createTeam(TeamFormDTO dto) {
        if (dto.getTeamType() == null) {
            throw new IllegalArgumentException("Team type must be specified");
        }

        return switch (dto.getTeamType()) {
            case MEDICAL -> createMedicalTeam(dto);
            case FIRE    -> createFireTeam(dto);
            case SEARCH_RESCUE -> createSearchRescueTeam(dto);
            case POLICE  -> createPoliceTeam(dto);
        };
    }

    private MedicalTeam createMedicalTeam(TeamFormDTO dto) {
        return new MedicalTeam(
                dto.getName(), dto.getLocation(), dto.getLatitude(), dto.getLongitude(),
                dto.getCapacity(), dto.getContactInfo(), dto.getDescription(),
                dto.getSpecialization(),
                Boolean.TRUE.equals(dto.getHasAmbulance()),
                dto.getNumberOfDoctors() != null ? dto.getNumberOfDoctors() : 0
        );
    }

    private FireTeam createFireTeam(TeamFormDTO dto) {
        return new FireTeam(
                dto.getName(), dto.getLocation(), dto.getLatitude(), dto.getLongitude(),
                dto.getCapacity(), dto.getContactInfo(), dto.getDescription(),
                dto.getEquipmentType(),
                dto.getWaterTankCapacity() != null ? dto.getWaterTankCapacity() : 0,
                Boolean.TRUE.equals(dto.getHasHazmatSuit())
        );
    }

    private SearchRescueTeam createSearchRescueTeam(TeamFormDTO dto) {
        return new SearchRescueTeam(
                dto.getName(), dto.getLocation(), dto.getLatitude(), dto.getLongitude(),
                dto.getCapacity(), dto.getContactInfo(), dto.getDescription(),
                dto.getSearchCapability(),
                Boolean.TRUE.equals(dto.getHasDogs()),
                Boolean.TRUE.equals(dto.getHasDrone())
        );
    }

    private PoliceTeam createPoliceTeam(TeamFormDTO dto) {
        return new PoliceTeam(
                dto.getName(), dto.getLocation(), dto.getLatitude(), dto.getLongitude(),
                dto.getCapacity(), dto.getContactInfo(), dto.getDescription(),
                dto.getJurisdictionArea(),
                dto.getNumberOfOfficers() != null ? dto.getNumberOfOfficers() : 0,
                Boolean.TRUE.equals(dto.getHasArmoredVehicle())
        );
    }

    /**
     * Applies DTO updates to an existing ResponseTeam (for edit operations).
     * Preserves the concrete subclass — type cannot be changed after creation.
     */
    public void updateTeamFromDTO(ResponseTeam team, TeamFormDTO dto) {
        team.setName(dto.getName());
        team.setLocation(dto.getLocation());
        team.setLatitude(dto.getLatitude());
        team.setLongitude(dto.getLongitude());
        team.setStatus(dto.getStatus());
        team.setCapacity(dto.getCapacity());
        team.setCurrentLoad(dto.getCurrentLoad() != null ? dto.getCurrentLoad() : team.getCurrentLoad());
        team.setContactInfo(dto.getContactInfo());
        team.setDescription(dto.getDescription());

        if (team instanceof MedicalTeam mt) {
            mt.setSpecialization(dto.getSpecialization());
            mt.setHasAmbulance(dto.getHasAmbulance());
            mt.setNumberOfDoctors(dto.getNumberOfDoctors());
        } else if (team instanceof FireTeam ft) {
            ft.setEquipmentType(dto.getEquipmentType());
            ft.setWaterTankCapacity(dto.getWaterTankCapacity());
            ft.setHasHazmatSuit(dto.getHasHazmatSuit());
        } else if (team instanceof SearchRescueTeam srt) {
            srt.setSearchCapability(dto.getSearchCapability());
            srt.setHasDogs(dto.getHasDogs());
            srt.setHasDrone(dto.getHasDrone());
        } else if (team instanceof PoliceTeam pt) {
            pt.setJurisdictionArea(dto.getJurisdictionArea());
            pt.setNumberOfOfficers(dto.getNumberOfOfficers());
            pt.setHasArmoredVehicle(dto.getHasArmoredVehicle());
        }
    }

    /**
     * Converts a ResponseTeam entity to a TeamFormDTO for pre-populating the edit form.
     */
    public TeamFormDTO toDTO(ResponseTeam team) {
        TeamFormDTO dto = new TeamFormDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLocation(team.getLocation());
        dto.setLatitude(team.getLatitude());
        dto.setLongitude(team.getLongitude());
        dto.setStatus(team.getStatus());
        dto.setCapacity(team.getCapacity());
        dto.setCurrentLoad(team.getCurrentLoad());
        dto.setContactInfo(team.getContactInfo());
        dto.setDescription(team.getDescription());
        dto.setTeamType(team.getTeamType());

        if (team instanceof MedicalTeam mt) {
            dto.setSpecialization(mt.getSpecialization());
            dto.setHasAmbulance(mt.getHasAmbulance());
            dto.setNumberOfDoctors(mt.getNumberOfDoctors());
        } else if (team instanceof FireTeam ft) {
            dto.setEquipmentType(ft.getEquipmentType());
            dto.setWaterTankCapacity(ft.getWaterTankCapacity());
            dto.setHasHazmatSuit(ft.getHasHazmatSuit());
        } else if (team instanceof SearchRescueTeam srt) {
            dto.setSearchCapability(srt.getSearchCapability());
            dto.setHasDogs(srt.getHasDogs());
            dto.setHasDrone(srt.getHasDrone());
        } else if (team instanceof PoliceTeam pt) {
            dto.setJurisdictionArea(pt.getJurisdictionArea());
            dto.setNumberOfOfficers(pt.getNumberOfOfficers());
            dto.setHasArmoredVehicle(pt.getHasArmoredVehicle());
        }
        return dto;
    }
}
