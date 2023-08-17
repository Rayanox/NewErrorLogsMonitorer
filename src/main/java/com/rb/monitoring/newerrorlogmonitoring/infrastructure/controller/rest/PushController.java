package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.rest;

import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PushController {

    private final PushService pushService;

    @GetMapping("/test")
    public String test() {
        try {
            pushService.updateDashboard();
            return "ok";
        }catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/getUiCount")
    public int getUiCount() {
        return pushService.getUiCount();
    }

    @GetMapping("/push")
    public String pushMessage(@RequestParam String message) {
        try {
            pushService.pushCustomNotification(message);
            return "ok";
        }catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
