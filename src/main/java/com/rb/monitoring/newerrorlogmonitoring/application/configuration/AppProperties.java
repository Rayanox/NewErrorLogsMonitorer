package com.rb.monitoring.newerrorlogmonitoring.application.configuration;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String applicationName;
    private String companyPackage;
    private String restUrlLogExposed;
    private Integer cleanUnseenLogsIntervalleDays;
    private Integer disableHourFrom;
    private Integer disableHourTo;

    private List<ServiceProperties> services;
    private List<EnvironmentProperties> environments;

    public ServiceProperties of(String serviceName) {
        return services.stream()
                .filter(serviceConf -> serviceConf.getServiceName().contains(serviceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
    }

}
