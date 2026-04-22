package com.disaster.response.repository;

import com.disaster.response.model.ResponseTeam;
import com.disaster.response.model.TeamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SRP: TeamRepository handles only team data persistence.
 * Returns all ResponseTeam subclasses (MedicalTeam, FireTeam, SearchRescueTeam, PoliceTeam)
 * thanks to SINGLE_TABLE inheritance — LSP in action at the data layer.
 */
@Repository
public interface ResponseTeamRepository extends JpaRepository<ResponseTeam, Long> {

    List<ResponseTeam> findByStatus(TeamStatus status);

    List<ResponseTeam> findAllByOrderByNameAsc();

    @Query("SELECT t FROM ResponseTeam t WHERE t.status = 'AVAILABLE' AND (t.capacity - t.currentLoad) > 0")
    List<ResponseTeam> findAvailableTeams();

    long countByStatus(TeamStatus status);
}
