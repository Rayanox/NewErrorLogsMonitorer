package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.rest;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Application;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ApplicationRepository;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class ApplicationController {

    @Value("${app.cron.minutes-intervalle}")
    private int cronIntervalle;
    private final ApplicationRepository applicationRepository;
    private final ServicesRepository servicesRepository;

    @GetMapping("/getAppInformations")
    public Application getAppInformations() {
        var application = applicationRepository.findAll().stream()
                .findAny()
                .orElseThrow();
        application.setNextProcessingDate(getNextProcessingDate(application.getLastProcessedDate()));
        return application;
    }

    @GetMapping("/getServicesInformations")
    public List<Service> getServicesInformations() {
        return servicesRepository.findAll();
    }

    /*
        PRIVATES
     */

    private LocalDateTime getNextProcessingDate(LocalDateTime lastProcessedDate) {
        return lastProcessedDate.plusMinutes(cronIntervalle);
    }
}
