package com.disaster.response.repository;

import com.disaster.response.model.Shelter;
import com.disaster.response.model.ShelterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    List<Shelter> findByStatus(ShelterStatus status);

    List<Shelter> findAllByOrderByNameAsc();

    @Query("SELECT s FROM Shelter s WHERE s.status = 'OPEN' AND s.currentOccupancy < s.capacity")
    List<Shelter> findAvailableShelters();

    long countByStatus(ShelterStatus status);

    @Query("SELECT COALESCE(SUM(s.currentOccupancy), 0) FROM Shelter s")
    long getTotalOccupancy();

    @Query("SELECT COALESCE(SUM(s.capacity), 0) FROM Shelter s")
    long getTotalCapacity();
}
