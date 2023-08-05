package com.rb.monitoring.newerrorlogmonitoring;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableJpaRepositories //FIXME
@EnableScheduling
@PropertySource(value = "classpath:services.properties")
@EnableConfigurationProperties({AppProperties.class})
public class NewErrorLogMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewErrorLogMonitoringApplication.class, args);
	}
}
