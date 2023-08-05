package com.rb.monitoring.newerrorlogmonitoring.application.configuration;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.ServiceProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String companyPackage;

    private List<ServiceProperties> services;

    public ServiceProperties of(String serviceName) {
        return services.stream()
                .filter(serviceConf -> serviceConf.getServiceName().contains(serviceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

}
