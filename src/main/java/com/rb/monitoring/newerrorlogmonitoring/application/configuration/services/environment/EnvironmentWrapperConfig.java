package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnvironmentWrapperConfig {

    private ServiceEnvironmentConfig serviceProperties;
    private EnvironmentProperties environmentProperties;

}
