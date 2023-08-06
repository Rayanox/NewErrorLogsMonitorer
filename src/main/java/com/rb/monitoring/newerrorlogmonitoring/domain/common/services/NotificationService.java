package com.rb.monitoring.newerrorlogmonitoring.domain.common.services;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
}
