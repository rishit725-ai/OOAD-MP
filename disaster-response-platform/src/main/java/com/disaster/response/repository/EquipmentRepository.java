package com.disaster.response.repository;

import com.disaster.response.model.Equipment;
import com.disaster.response.model.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByStatus(EquipmentStatus status);

    List<Equipment> findByCategory(String category);

    List<Equipment> findAllByOrderByNameAsc();

    long countByStatus(EquipmentStatus status);
}
