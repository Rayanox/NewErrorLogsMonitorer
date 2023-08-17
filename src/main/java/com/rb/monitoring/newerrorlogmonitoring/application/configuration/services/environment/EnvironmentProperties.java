package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment;

import lombok.Data;

@Data
public class EnvironmentProperties {

    private String name;
    private String longName;
    private boolean preprod;
    private Integer unseenCheckedHours;
    private String squad;

}
