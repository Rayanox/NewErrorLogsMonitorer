package com.rb.monitoring.newerrorlogmonitoring.application;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfItem;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Application;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.LogService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer.LogConsumer;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ApplicationRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.EnvironmentRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.LogEntryRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class Core {

    private final LogConsumer logConsumer;
    private final LogService logService;
    private final List<ServiceConfItem> servicesConf;
    private final ServicesRepository servicesRepository;
    private final NotificationService notifications;
    private final ApplicationRepository applicationRepository;
    private final EnvironmentRepository environmentRepository;
    private final LogEntryRepository logEntryRepository;
    private final AppProperties appProperties;

    public void process() {
        var application = getApplication();

        for (var service : servicesConf) {
            var serviceDb = getDbServiceOrCreate(service);

            for (var environment : service.getEnvironments()) {
                try {
                    var environmentDb = getDbEnvironmentOrCreate(serviceDb, environment);
                    var logEntriesDb = environmentDb.getLogEntries();

                    var serverLogs = logConsumer.fetchLogs(environment);
                    var serverLogEntries = logService.readLogs(serverLogs, environmentDb.getLastReadDate(), service, environment);
                    var newLogsDetected = logService.getNewLogs(logEntriesDb, serverLogEntries);

                    prepareEntities(newLogsDetected, serverLogEntries, environmentDb, logEntriesDb);
                    newLogsDetected = logEntryRepository.saveAll(newLogsDetected);

                    logService.getDbLogsSeenOnServer(serverLogEntries, logEntriesDb).stream()
                            .peek(logSeenDb -> logSeenDb.getStatus().setLastSeenDate(LocalDateTime.now()))
                            .forEach(logSeenDb -> logEntryRepository.save(logSeenDb));

                    cleanDbLogsIfNecessary(logEntriesDb);
                    processNotifications(newLogsDetected, environmentDb);
                }catch (ServerDownException e) {
                    notifications.notifySubscribers(e);
                }
            }
        }
        logEntryRepository.flush();
        updateApplicationState(application);
    }

    private void cleanDbLogsIfNecessary(List<LogEntry> logEntriesDb) {
        var logsToClean = logEntriesDb.stream()
                .filter(logEntry -> logEntry.getStatus().getInsertionDate().isBefore(LocalDateTime.now().minusDays(appProperties.getCleanUnseenLogsIntervalleDays())))
                .collect(Collectors.toList());

        if(!logsToClean.isEmpty()) {
            notifications.notifyInfoCleanLogs(logsToClean);
            logEntryRepository.deleteAll(logsToClean);
        }
    }

    private Service getDbServiceOrCreate(ServiceConfItem service) {
        var dbService = servicesRepository.findByServiceName(service.getServiceName());
        if(dbService.isEmpty()) {
            var newService = servicesRepository.saveAndFlush(Service.newService(service));
            return newService;
        }
        return dbService.get();
    }

    private void processNotifications(List<LogEntry> newLogsDetected, Environment environmentDb) {
        if(environmentDb.isFirstIndexed()) {
            notifications.notifySubscribers(newLogsDetected);
        }else {
            environmentDb.setFirstIndexedDate(LocalDateTime.now());
            environmentRepository.save(environmentDb);
        }
    }


    private void prepareEntities(List<LogEntry> newLogsDetected, List<LogEntry> serverLogEntries, Environment environmentDb, List<LogEntry> logEntriesDb) {
        newLogsDetected.forEach(logEntry ->  {
            logEntry.getStatus().setInsertionDate(LocalDateTime.now());
            logEntry.setEnvironment(environmentDb);
            logEntry.getStatus().setHasToBeChecked(environmentDb.isFirstIndexed());
            logEntry.getStatus().setFirstIndexation(!environmentDb.isFirstIndexed());

            logEntriesDb.add(logEntry);
        });
        if(!serverLogEntries.isEmpty()) {
            environmentDb.setLastReadDate(serverLogEntries.get(serverLogEntries.size() - 1).getDate());
            environmentRepository.save(environmentDb);
        }
    }

    private Application getApplication() {
        var applicationSaved = applicationRepository.findAll().stream().findFirst();
        if(applicationSaved.isEmpty()) {
            var newApplication = Application.builder()
                    .build();
            applicationRepository.saveAndFlush(newApplication);
            return newApplication;
        }
        return applicationSaved.get();
    }

    private void updateApplicationState(Application application) {
        if(!application.isFirstIndexed()) {
            application.setFirstIndexationDate(LocalDateTime.now());
            log.info("First indexation done");
        }
        application.setLastProcessedDate(LocalDateTime.now());
        applicationRepository.saveAndFlush(application);
    }

    public Environment getDbEnvironmentOrCreate(Service serviceDb, EnvironmentWrapperConfig environment) {
        var environmentDb = serviceDb.getEnvironments().stream()
                .filter(env -> env.getEnvironmentName().equals(environment.getEnvironmentProperties().getName()))
                .findFirst();

        if(environmentDb.isEmpty()) {
            var newEnvironment = Environment.builder()
                    .environmentName(environment.getEnvironmentProperties().getName())
                    .prettyEnvironmentName(environment.getEnvironmentProperties().getLongName())
                    .logEntries(new ArrayList<>())
                    .service(serviceDb)
                    .build();
            serviceDb.getEnvironments().add(newEnvironment);
            environmentRepository.saveAndFlush(newEnvironment);
            return newEnvironment;
        }
        return environmentDb.get();
    }

}
