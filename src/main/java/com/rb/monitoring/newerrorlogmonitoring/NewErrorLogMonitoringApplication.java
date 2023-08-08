package com.rb.monitoring.newerrorlogmonitoring;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.IhmProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.notifications.MailProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
@EnableConfigurationProperties({AppProperties.class, MailProperties.class, IhmProperties.class})
@PropertySources({
		@PropertySource("classpath:services.properties"),
		@PropertySource("classpath:mail-notifications.properties"),
		@PropertySource("classpath:environments.properties")
})
public class NewErrorLogMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewErrorLogMonitoringApplication.class, args);
	}
}
