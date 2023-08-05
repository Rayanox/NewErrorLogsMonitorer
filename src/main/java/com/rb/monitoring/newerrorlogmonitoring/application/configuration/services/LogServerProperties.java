package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import lombok.Data;

@Data
public class LogServerProperties {

    private String url;
    private String basicAuthToken;

}
