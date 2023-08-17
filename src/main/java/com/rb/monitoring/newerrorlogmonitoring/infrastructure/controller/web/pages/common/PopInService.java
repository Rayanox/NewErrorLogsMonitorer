package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;

@UIScope
@SpringComponent
@RequiredArgsConstructor
public class PopInService {

    public void showNotImplementedPopin() {
        showPopIn("Not implemented yet", LogLevel.WARN);
    }

    public void showPopIn(String message, LogLevel logLevel) {
        Notification notification = Notification.show(message);
        switch (logLevel) {
            case ERROR:
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                break;
            case WARN:
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                break;
            case INFO:
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                break;
            case TRACE:
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                break;
            default:
                Notification notificationError = Notification.show("Log level not recognized - please adapt the Switch statement in PopInService.java");
                notificationError.addThemeVariants(NotificationVariant.LUMO_ERROR);
                break;
        }
    }

}
