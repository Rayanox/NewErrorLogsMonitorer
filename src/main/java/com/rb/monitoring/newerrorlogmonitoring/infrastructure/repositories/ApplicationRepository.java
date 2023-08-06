package com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
