package com.rb.monitoring.newerrorlogmonitoring.application;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ExceptionEntity;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.ExceptionUtil;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Log4j2
@Component
@Profile("!test")
@RequiredArgsConstructor
public class Schedules {

    private final Core core;
    private final ExceptionRepository exceptionRepository;
    private final NotificationService notifications;

    @Scheduled(fixedDelayString = "#{${app.cron.minutes-intervalle} * 60000}") //Every 2 minutes
    public void cron() {
        try {
            core.process();
        } catch (Exception e) {
            persistException(e);
            notifications.notifySubscribers(new Exception("Error catched by cron", e));
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
