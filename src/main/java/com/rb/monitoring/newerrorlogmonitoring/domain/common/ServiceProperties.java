package com.rb.monitoring.newerrorlogmonitoring.domain.common;

import lombok.Data;

@Data
public class ServiceProperties {

    private String serviceName;
    private String patternNewEntry;
    private String patternStackTraceLine;
    private String patternExceptionMessageErrorNetwork;

    private String patternDate;
    private String patternClassName;
    private String patternMessage;
    private String patternLogLevel;
    private String patternCausedByMessage;
    private String patternDateFormatter;

}
