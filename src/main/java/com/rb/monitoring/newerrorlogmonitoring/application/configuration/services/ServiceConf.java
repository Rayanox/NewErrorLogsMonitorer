package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.ServiceEnvironmentConfig;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ServiceConf {

    @Bean
    public List<ServiceConfItem> servicesConf(AppProperties appProperties) {
        return appProperties.getServices().stream()
                .map(service -> mapToServiceConfItem(service, appProperties.getEnvironments()))
                .toList();
    }


    /*
        PRIVATES
     */

    private ServiceConfItem mapToServiceConfItem(ServiceProperties service, List<EnvironmentProperties>  environmentProperties) {
        return ServiceConfItem.builder()
                .serviceName(service.getServiceName())
                .patterns(service.getPatterns())
                .environments(mapToEnvironmentWrappers(service, environmentProperties))
                .build();
    }

    private List<EnvironmentWrapperConfig> mapToEnvironmentWrappers(ServiceProperties service, List<EnvironmentProperties>  environmentProperties) {
        return service.getPropertiesByEnvironment().entrySet().stream()
                .map(entry -> mapToEnvironmentWrapper(entry, environmentProperties))
                .toList();
    }

    private EnvironmentWrapperConfig mapToEnvironmentWrapper(java.util.Map.Entry<String, ServiceEnvironmentConfig> entry, List<EnvironmentProperties>  environmentProperties) {
        return EnvironmentWrapperConfig.builder()
                .environmentProperties(mapToEnvironmentProperties(entry.getKey(), environmentProperties))
                .serviceProperties(entry.getValue())
                .build();
    }

    private EnvironmentProperties mapToEnvironmentProperties(String environmentName, List<EnvironmentProperties>  environmentProperties) {
        return environmentProperties.stream()
                .filter(environment -> environment.getName().equals(environmentName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Environment '"+ environmentName +"' not found in environment properties."));
    }

}
