package com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ExceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionRepository extends JpaRepository<ExceptionEntity, Long> {
}
