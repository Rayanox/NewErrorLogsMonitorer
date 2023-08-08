package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ServiceConfItem {

    private String serviceName;
    private LogPatternProperties patterns;
    private List<EnvironmentWrapperConfig> environments;
}
