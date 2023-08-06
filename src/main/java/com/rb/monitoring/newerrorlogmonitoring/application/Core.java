package com.rb.monitoring.newerrorlogmonitoring.application;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Application;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.LogService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer.LogConsumer;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ApplicationRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class Core {

    private final LogConsumer logConsumer;
    private final LogService logService;
    private final AppProperties appProperties;
    private final ServicesRepository servicesRepository;
    private final NotificationService notifications;
    private final ApplicationRepository applicationRepository;

    public void process() {
        var application = getApplication();

        for (var service : appProperties.getServices()) {
            try {
                var serviceDb = servicesRepository.findByServiceName(service.getServiceName())
                        .orElse(Service.newService(service));
                var logEntriesDb = serviceDb.getLogEntries();

                var serverLogs = logConsumer.fetchLogs(service);
                var serverLogEntries = logService.readLogs(serverLogs, serviceDb.getLastReadDate(), service);
                var newLogsDetected = logService.getNewLogs(logEntriesDb, serverLogEntries);

                prepareEntities(newLogsDetected, serverLogEntries, serviceDb, logEntriesDb);
                servicesRepository.save(serviceDb);

                if(application.isFirstIndexed()) {
                    notifications.notifySubscribers(newLogsDetected);
                }
            }catch (ServerDownException e) {
                notifications.notifySubscribers(e);
            }
        }
        servicesRepository.flush();
        updateApplicationState(application);
    }

    private void prepareEntities(List<LogEntry> newLogsDetected, List<LogEntry> serverLogEntries, Service serviceDb, List<LogEntry> logEntriesDb) {
        newLogsDetected.forEach(logEntry ->  {
            logEntry.setService(serviceDb);
            logEntriesDb.add(logEntry);
        });
        if(!serverLogEntries.isEmpty()) {
            serviceDb.setLastReadDate(serverLogEntries.get(serverLogEntries.size() - 1).getDate());
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
        }
        application.setLastProcessedDate(LocalDateTime.now());
        applicationRepository.saveAndFlush(application);
    }
}
