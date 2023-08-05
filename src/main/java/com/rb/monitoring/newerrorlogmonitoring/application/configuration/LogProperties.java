package com.rb.monitoring.newerrorlogmonitoring.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.logs")
public class LogProperties {


    private String baseUrlTemplate;

}
