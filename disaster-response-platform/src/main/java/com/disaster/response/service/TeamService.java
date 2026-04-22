package com.disaster.response.service;

import com.disaster.response.dto.TeamFormDTO;
import com.disaster.response.factory.ResponseTeamFactory;
import com.disaster.response.model.ResponseTeam;
import com.disaster.response.model.TeamStatus;
import com.disaster.response.repository.ResponseTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SRP: TeamService handles all response team business logic.
 * Uses ResponseTeamFactory to create team instances (Factory pattern).
 */
@Service
@Transactional
public class TeamService {

    private final ResponseTeamRepository teamRepository;
    private final ResponseTeamFactory teamFactory;

    public TeamService(ResponseTeamRepository teamRepository, ResponseTeamFactory teamFactory) {
        this.teamRepository = teamRepository;
        this.teamFactory = teamFactory;
    }

    public ResponseTeam createTeam(TeamFormDTO dto) {
        ResponseTeam team = teamFactory.createTeam(dto);
        return teamRepository.save(team);
    }

    public ResponseTeam updateTeam(Long id, TeamFormDTO dto) {
        ResponseTeam existing = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + id));
        teamFactory.updateTeamFromDTO(existing, dto);
        return teamRepository.save(existing);
    }

    public Optional<ResponseTeam> findById(Long id) {
        return teamRepository.findById(id);
    }

    public List<ResponseTeam> findAll() {
        return teamRepository.findAllByOrderByNameAsc();
    }

    public List<ResponseTeam> findAvailable() {
        return teamRepository.findAvailableTeams();
    }

    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    public TeamFormDTO toDTO(ResponseTeam team) {
        return teamFactory.toDTO(team);
    }

    // ===== Dashboard statistics =====
    public long countByStatus(TeamStatus status) {
        return teamRepository.countByStatus(status);
    }

    public long countTotal() {
        return teamRepository.count();
    }
}
