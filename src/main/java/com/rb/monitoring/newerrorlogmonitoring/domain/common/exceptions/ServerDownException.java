package com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import lombok.Getter;

@Getter
public class ServerDownException extends RuntimeException {

    private ServiceProperties serviceProps;

    public ServerDownException(ServiceProperties service, ServiceProperties serviceProperties) {
        super(String.format("Server %s is DOWN on PREPROD. Go see the logs: %s", service.getServiceName(), service.getLogServers().getUrl()));
        this.serviceProps = serviceProperties;
    }

}
