package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.LogServerProperties;
import lombok.Data;

@Data
public class ServiceEnvironmentConfig {

    private LogServerProperties logServers;

}
