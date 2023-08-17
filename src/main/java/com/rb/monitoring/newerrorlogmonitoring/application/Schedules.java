package com.rb.monitoring.newerrorlogmonitoring.application;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ExceptionEntity;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.ExceptionUtil;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@Profile("!test")
@RequiredArgsConstructor
public class Schedules {

    private final AppProperties appProperties;
    private final Core core;
    private final ExceptionRepository exceptionRepository;
    private final NotificationService notifications;
    private final StatusService statusService;

    @Scheduled(fixedDelayString = "#{${app.cron.minutes-intervalle} * 60000}")
    public void cron() {
        if(isDisablePeriod()) {
            log.debug("Cron is disabled");
            return;
        }

        try {
            core.process();
        } catch (Exception e) {
            persistException(e);
            notifications.notifySubscribers(new Exception("Error catched by cron", e));
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void logCheckCleaner() {
        if(isDisablePeriod()) {
            log.debug("Cron 'logCheckCleaner' is disabled");
            return;
        }
        statusService.processLogCheckClean();
    }

    /*
        PRIVATES
     */

    private boolean isDisablePeriod() {
        LocalTime now = LocalTime.now();
        LocalTime disableHourFrom = LocalTime.of(appProperties.getDisableHourFrom(), 0);
        LocalTime disableHourTo = LocalTime.of(appProperties.getDisableHourTo(), 0);

        if (disableHourFrom.isAfter(disableHourTo)) {
            if (now.isBefore(disableHourTo) || now.isAfter(disableHourFrom)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (now.isBefore(disableHourTo) && now.isAfter(disableHourFrom)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*
        PRIVATES
     */

    private void persistException(Exception e) {
        ExceptionEntity exceptionEntity = ExceptionEntity.builder()
                .exceptionClassName(e.getClass().getSimpleName())
                .message(ExceptionUtil.getMessageRecursively(e))
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .dateTime(LocalDateTime.now())
                .build();

        exceptionRepository.saveAndFlush(exceptionEntity);
    }
}
