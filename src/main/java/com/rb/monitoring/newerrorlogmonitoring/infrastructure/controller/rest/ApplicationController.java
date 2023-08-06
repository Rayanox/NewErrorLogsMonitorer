package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.rest;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Application;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class ApplicationController {

    @Value("${app.cron.minutes-intervalle}")
    private int cronIntervalle;
    private final ApplicationRepository applicationRepository;

    @GetMapping("/getAppStatus")
    public Application getAppStatus() {
        var application = applicationRepository.findAll().stream()
                .findAny()
                .orElseThrow();
        application.setNextProcessingDate(getNextProcessingDate(application.getLastProcessedDate()));
        return application;
    }

    /*
        PRIVATES
     */

    private LocalDateTime getNextProcessingDate(LocalDateTime lastProcessedDate) {
        return lastProcessedDate.plusMinutes(cronIntervalle);
    }
}
