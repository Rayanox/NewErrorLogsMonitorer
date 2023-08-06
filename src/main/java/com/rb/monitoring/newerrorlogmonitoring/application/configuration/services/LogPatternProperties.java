package com.rb.monitoring.newerrorlogmonitoring.application.configuration.services;

import lombok.Data;

@Data
public class LogPatternProperties {
    private String patternNewEntry;
    private String patternStackTraceLine;
    private String patternExceptionMessageErrorNetwork;
    private String patternDeadServer;

    private String patternDate;
    private String patternClassName;
    private String patternMessage;
    private String patternLogLevel;
    private String patternCausedByMessage;
    private String patternDateFormatter;
}
