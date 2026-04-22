package com.disaster.response.service;

import com.disaster.response.model.Shelter;
import com.disaster.response.model.ShelterStatus;
import com.disaster.response.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SRP: ShelterService handles all shelter business logic.
 * LoD: Only communicates with ShelterRepository directly.
 */
@Service
@Transactional
public class ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public Shelter createShelter(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public Shelter updateShelter(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public Optional<Shelter> findById(Long id) {
        return shelterRepository.findById(id);
    }

    public List<Shelter> findAll() {
        return shelterRepository.findAllByOrderByNameAsc();
    }

    public List<Shelter> findAvailable() {
        return shelterRepository.findAvailableShelters();
    }

    public void openShelter(Long id) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + id));
        shelter.setStatus(ShelterStatus.OPEN);
        shelter.setOpenedAt(LocalDateTime.now());
        shelterRepository.save(shelter);
    }

    public void closeShelter(Long id) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + id));
        shelter.setStatus(ShelterStatus.CLOSED);
        shelter.setClosedAt(LocalDateTime.now());
        shelterRepository.save(shelter);
    }

    public void updateOccupancy(Long id, int delta) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + id));
        int newOccupancy = Math.max(0, shelter.getCurrentOccupancy() + delta);
        shelter.setCurrentOccupancy(Math.min(newOccupancy, shelter.getCapacity()));
        if (shelter.isFull()) {
            shelter.setStatus(ShelterStatus.FULL);
        } else if (shelter.getStatus() == ShelterStatus.FULL) {
            shelter.setStatus(ShelterStatus.OPEN);
        }
        shelterRepository.save(shelter);
    }

    public void deleteById(Long id) {
        shelterRepository.deleteById(id);
    }

    // ===== Dashboard statistics =====
    public long countByStatus(ShelterStatus status) {
        return shelterRepository.countByStatus(status);
    }

    public long getTotalOccupancy() {
        return shelterRepository.getTotalOccupancy();
    }

    public long getTotalCapacity() {
        return shelterRepository.getTotalCapacity();
    }

    public long countTotal() {
        return shelterRepository.count();
    }
}
