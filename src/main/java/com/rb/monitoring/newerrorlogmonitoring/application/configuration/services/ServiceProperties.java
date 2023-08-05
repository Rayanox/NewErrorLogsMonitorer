package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import lombok.Data;

@Data
public class ServiceProperties {

    private String serviceName;

    private LogPatternProperties patterns;
    private LogServerProperties logServers;
}
