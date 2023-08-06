package com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicesRepository extends JpaRepository<Service, Integer> {

    Optional<Service> findByServiceName(String serviceName);

}
