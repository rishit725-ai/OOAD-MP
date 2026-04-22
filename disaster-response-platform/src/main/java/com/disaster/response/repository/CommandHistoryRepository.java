package com.disaster.response.repository;

import com.disaster.response.model.CommandHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandHistoryRepository extends JpaRepository<CommandHistory, Long> {

    List<CommandHistory> findAllByOrderByExecutedAtDesc();

    List<CommandHistory> findTop20ByOrderByExecutedAtDesc();
}
