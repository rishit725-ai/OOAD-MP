package com.disaster.response.service;

import com.disaster.response.model.Equipment;
import com.disaster.response.model.EquipmentStatus;
import com.disaster.response.model.ResponseTeam;
import com.disaster.response.repository.EquipmentRepository;
import com.disaster.response.repository.ResponseTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * SRP: EquipmentService handles all equipment business logic.
 */
@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ResponseTeamRepository teamRepository;

    public EquipmentService(EquipmentRepository equipmentRepository,
                            ResponseTeamRepository teamRepository) {
        this.equipmentRepository = equipmentRepository;
        this.teamRepository = teamRepository;
    }

    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    public Optional<Equipment> findById(Long id) {
        return equipmentRepository.findById(id);
    }

    public List<Equipment> findAll() {
        return equipmentRepository.findAllByOrderByNameAsc();
    }

    public void assignToTeam(Long equipmentId, Long teamId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + equipmentId));
        ResponseTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        equipment.setAssignedTeam(team);
        equipment.setStatus(EquipmentStatus.IN_USE);
        equipmentRepository.save(equipment);
    }

    public void releaseFromTeam(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + equipmentId));
        equipment.setAssignedTeam(null);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipmentRepository.save(equipment);
    }

    public void updateStatus(Long id, EquipmentStatus status) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + id));
        equipment.setStatus(status);
        equipmentRepository.save(equipment);
    }

    public void deleteById(Long id) {
        equipmentRepository.deleteById(id);
    }

    // ===== Dashboard statistics =====
    public long countByStatus(EquipmentStatus status) {
        return equipmentRepository.countByStatus(status);
    }

    public long countTotal() {
        return equipmentRepository.count();
    }
}
