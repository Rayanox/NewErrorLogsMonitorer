package com.rb.monitoring.newerrorlogmonitoring.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Schedules {


    private final Core core;
//    private final ExceptionService exceptionService;//FIXME


    //Every 2 hours
    @Scheduled(fixedDelay = 3600*2*1000)
    public void cron() {
        try {
                core.process();
        } catch (Exception e) {
//                exceptionService.insertException(e); //FIXME
            e.printStackTrace();
        }
    }


}
