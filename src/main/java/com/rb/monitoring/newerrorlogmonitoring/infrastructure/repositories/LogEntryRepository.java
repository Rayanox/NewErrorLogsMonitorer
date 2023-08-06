package com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories;

import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findAllByDateGreaterThan(LocalDateTime from);
}
