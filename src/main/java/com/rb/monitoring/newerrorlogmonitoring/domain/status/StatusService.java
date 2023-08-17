package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfResolver;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PushService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.LogEntryRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final NotificationService notificationService;
    private final LogEntryRepository logEntryRepository;
    private final ServiceConfResolver serviceConfResolver;
    private final PushService pushService;

    @Value("${app.log-checker-cleaner.unseen-duration-hours}")
    private int unseenDurationCleanerHours;

    public StatusEnum getStatus(Environment environment) {
        var isErrorStatus = environment.getLogEntries().stream()
                .map(LogEntry::getStatus)
                .filter(this::pertinentLog)
                .anyMatch(status -> !status.isChecked());

        if(isErrorStatus) {
            return StatusEnum.ERROR_DETECTED;
        }

        return environment.getLogEntries().stream()
                .map(LogEntry::getStatus)
                .filter(this::pertinentLog)
                .findAny()
                .map(status -> StatusEnum.ERROR_CHECKED)
                .orElse(StatusEnum.OK);
    }

    /**
     * Can throw an exception if a concurrent user or the monthly cleaner-indexer Scheduler deleted the status/entries.
     * @param environment
     * @throws Exception
     */
    public void checkLogEntriesOfEnvironment(Environment environment) throws Exception {
        var logEntries = environment.getLogEntries().stream()
                .map(LogEntry::getStatus)
                .filter(entry -> !entry.isPersistentIndexed())
                .peek(status -> status.setChecked(true))
                .toList();

        statusRepository.saveAll(logEntries);
    }

    @Transactional
    public void processLogCheckClean() {
        var allCheckedLogs = logEntryRepository.findAll()
                .stream()
                .filter(logEntry -> !logEntry.getStatus().isPersistentIndexed())
                .filter(logEntry -> logEntry.getStatus().isChecked())
                .toList();

        if(allCheckedLogs.isEmpty()) {
            log.debug("No checked logs are present in database");
            return;
        }

        var allUnseenLogsForLimitDuration = getUnseenLogsForLimitDuration(allCheckedLogs);

        if(allUnseenLogsForLimitDuration.isEmpty()) {
            log.debug("Check logs exists but no of its were cleaned this time");
            return;
        }

        logEntryRepository.deleteAll(allUnseenLogsForLimitDuration);
        notificationService.notifyInfoCleanLogs(allUnseenLogsForLimitDuration);
        pushService.updateDashboard();
    }

    public void index(Environment environment) {
        var statusIndex = logEntryRepository.findAllByEnvironmentId(environment.getId()).stream()
                .filter(logEntry -> !logEntry.getStatus().isPersistentIndexed())
                .map(LogEntry::getStatus)
                .peek(status -> status.setPersistentIndexed(true))
                .toList();

        statusRepository.saveAll(statusIndex);
    }

    public void reset(Environment environment) {
        var statusReset = logEntryRepository.findAllByEnvironmentId(environment.getId()).stream()
                .filter(logEntry -> !logEntry.getStatus().isPersistentIndexed())
                .map(LogEntry::getStatus)
                .peek(status -> status.setPersistentIndexed(true))
                .toList();

        statusRepository.saveAll(statusReset);
    }

    public int getUnseenDurationCleanerHours(Environment environment) {
        var environmentServiceConfig = serviceConfResolver.getEnvironmentServiceConfig(environment);
        var durationFromEnvConf = environmentServiceConfig.getEnvironmentProperties().getUnseenCheckedHours();
        if(Objects.nonNull(durationFromEnvConf)) {
            return durationFromEnvConf;
        }
        return unseenDurationCleanerHours;
    }



    /*
        PRIVATE
     */

    private boolean pertinentLog(Status status) {
        return !status.isPersistentIndexed();
    }

    private List<LogEntry> getUnseenLogsForLimitDuration(List<LogEntry> allCheckedLogs) {
        var now = LocalDateTime.now();

        return allCheckedLogs.stream()
                .map(logEntry -> new LimitDurationWrapper(logEntry, now.minusHours(getUnseenDurationCleanerHours(logEntry.getEnvironment()))))
                .filter(wrapper -> wrapper.logEntry.getStatus().getLastSeenDate().isBefore(wrapper.unseenDurationLimitDate))
                .map(LimitDurationWrapper::logEntry)
                .toList();
    }

    private record LimitDurationWrapper(LogEntry logEntry, LocalDateTime unseenDurationLimitDate) {}
}
