package com.rb.monitoring.newerrorlogmonitoring.domain.common.services;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.UnclassifiedLogException;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMailService mailService;

    public void notifySubscribers(List<LogEntry> logEntries) {
        logEntries.forEach(logEntry -> {
            log.info("New errors detected: {}", logEntry);
            mailService.sendMail(logEntry);
        });
    }

    public void notifySubscribers(ServerDownException e) {
        log.error(e.getMessage());
        mailService.sendMail(e);
    }

    public void notifySubscribers(Exception e) {
        log.error(e.getMessage());
        mailService.sendMail(e);
    }

    public void notifySubscribers(UnclassifiedLogException e) {
        log.warn(e);
        mailService.sendMail(e);
    }

    public void notifyWarnSeenLog(LogEntry newLog) {
        var message = String.format("The following log has neither been detected as new log, nor been found in the existing logs (in DB). It is probably a normal behaviour. Disable notification when you will validate that this is never an error. Log entry = %s", newLog);
        log.info(message);
        mailService.sendMail(message, LogLevel.WARN, true);
    }

    public void notifyInfoCleanLogs(List<LogEntry> logsToClean) {
        var message = String.format("The following logs have been cleaned from the database because they are not appearing anymore. Logs = %s", logsToClean);
        log.info(message);
        mailService.sendMail(message, LogLevel.INFO, true);
    }

    public void notifyAdminCustom(String message, LogLevel warn) {
        log.info(message);
        mailService.sendMail(message, warn, true);
    }
}
