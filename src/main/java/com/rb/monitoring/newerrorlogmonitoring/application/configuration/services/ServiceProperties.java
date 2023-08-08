package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.ServiceEnvironmentConfig;
import lombok.Data;

import java.util.Map;

@Data
public class ServiceProperties {

    private String serviceName;

    private LogPatternProperties patterns;
    private Map<String, ServiceEnvironmentConfig> servicePropertiesByEnvironment;
}
