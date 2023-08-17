package com.rb.monitoring.newerrorlogmonitoring;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.IhmProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.notifications.MailProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentProperties;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.shared.communication.PushMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Push(PushMode.MANUAL)
@EnableScheduling
@EnableJpaRepositories
@EnableConfigurationProperties({AppProperties.class, MailProperties.class, IhmProperties.class})
@PropertySources({
		@PropertySource("classpath:services.properties"),
		@PropertySource("classpath:mail-notifications.properties"),
		@PropertySource("classpath:environments.properties")
})
@SpringBootApplication
public class NewErrorLogMonitoringApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(NewErrorLogMonitoringApplication.class, args);
	}
}
