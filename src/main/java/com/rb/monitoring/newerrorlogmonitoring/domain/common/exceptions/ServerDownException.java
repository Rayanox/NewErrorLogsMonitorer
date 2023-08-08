package com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfItem;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import lombok.Getter;

@Getter
public class ServerDownException extends RuntimeException {

    private String serviceName;

    public ServerDownException(ServiceConfItem service, EnvironmentWrapperConfig environment) {
        super(String.format("Server %s is DOWN on PREPROD. Go see the logs: %s", service.getServiceName(), environment.getServiceProperties().getLogServers().getUrl()));
        this.serviceName = service.getServiceName();
    }

}
