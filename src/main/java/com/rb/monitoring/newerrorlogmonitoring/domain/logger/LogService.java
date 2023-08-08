package com.rb.monitoring.newerrorlogmonitoring.domain.logger;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfItem;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.factory.LogsFactoryFromStart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogService {

    private final AppProperties appProperties;
    private final NewLogValidatorRule newLogValidatorRule;
    private final NotificationService notificationService;

    public List<LogEntry> readLogs(List<String> logInputUpdated, LocalDateTime date, ServiceConfItem serviceConf, EnvironmentWrapperConfig environment) {
        List<LogEntry> logs = Objects.isNull(date)
                ? readAllLogs(logInputUpdated, serviceConf, environment)
                : readAllLogs(logInputUpdated, date, serviceConf, environment);

        return logs.stream()
                .distinct()
                .toList();
    }

    public List<LogEntry> readAllLogs(List<String> logs, ServiceConfItem serviceConf, EnvironmentWrapperConfig environment) {
        return LogsFactoryFromStart
                .newFactory(logs, serviceConf, appProperties, notificationService, environment)
                .process();
    }

    public List<LogEntry> readAllLogs(List<String> logInputUpdated, LocalDateTime date, ServiceConfItem serviceConf, EnvironmentWrapperConfig environment) {
        return LogsFactoryFromStart
                .newFactory(logInputUpdated, date, serviceConf, appProperties, notificationService, environment)
                .process();
    }

    public List<LogEntry> getNewLogs(List<LogEntry> oldLogs, List<LogEntry> newLogsFromServer) {
        return newLogsFromServer.parallelStream()
                .filter(newLog -> newLogValidatorRule.isNewLog(oldLogs, newLog))
                .collect(Collectors.toList());
    }

    /**
     * This method is used to get the logs that were saved and seen by the log server scanning. It is intended to be used
     * for updating the lastSeenDate of log entries in DB and later make the application able to clean the logs that are
     * not appearing anymore.
     *
     * @param oldLogs all logs saved in database
     * @param newLogsUndetected the new logs from server that were not detected as new errors
     * @return the list of logs that were saved and seen by the log server scanning
     */
    public List<LogEntry> getDbLogsSeenOnServer(List<LogEntry> oldLogs, List<LogEntry> newLogsUndetected) {
        return newLogsUndetected.stream()
                .filter(newLog -> oldLogs.contains(newLog))
                .map(newLog -> oldLogs.stream()
                        .filter(oldLog -> oldLog.equals(newLog))
                        .findFirst()
                        .orElseGet(() -> processNotificationWarn(newLog)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private LogEntry processNotificationWarn(LogEntry newLog) {
        notificationService.notifyWarnSeenLog(newLog);
        return null;
    }
}
