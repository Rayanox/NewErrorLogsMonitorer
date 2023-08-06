package com.rb.monitoring.newerrorlogmonitoring.application.configuration.notifications;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.Subscribers;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.notifications.mail")
public class MailProperties {

    private String from;
    private String subjectHeader;
    private Subscribers subscribers;

}
