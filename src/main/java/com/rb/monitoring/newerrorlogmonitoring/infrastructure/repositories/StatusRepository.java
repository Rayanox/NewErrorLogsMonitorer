package com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories;

import com.rb.monitoring.newerrorlogmonitoring.domain.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository extends JpaRepository<Status, Long> {
}
