package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ServiceConfResolver {

    private List<ServiceConfItem> servicesConf;

    public EnvironmentWrapperConfig getEnvironmentServiceConfig(Environment environment) {
        return servicesConf.stream()
                .filter(serviceConf -> serviceConf.getServiceName().equals(environment.getService().getServiceName()))
                .flatMap(serviceConf -> serviceConf.getEnvironments().stream())
                .filter(environmentWrapperConfigs -> environmentWrapperConfigs.getEnvironmentProperties().getName().equals(environment.getEnvironmentName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Environment '"+ environment.getEnvironmentName() +"' not found in service properties."));
    }
}
