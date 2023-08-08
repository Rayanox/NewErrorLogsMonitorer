package com.rb.monitoring.newerrorlogmonitoring.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.ihm")
public class IhmProperties {

    private Integer disableWarningStatusAfterMinutes;

}
