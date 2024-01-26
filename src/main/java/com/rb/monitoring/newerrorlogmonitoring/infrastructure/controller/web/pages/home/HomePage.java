package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfResolver;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.components.header.HeaderComponent;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.utils.resources.ResourceUtils;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.utils.resources.ResourcesPaths;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PopInService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PushService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components.MainHpComponent;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components.StatusDashboard;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;

@CssImport(value = "./styles/style.css")

//@NpmPackage(value = "jquery", version = "3.7.0")
//@JavaScript("./js/jquery.titlealert.js")

@Route("/")
public class HomePage extends VerticalLayout {

    private PopInService popInService;
    private PushService pushService;
    private ServicesRepository servicesRepository;
    private StatusService statusService;
    private NotificationService notificationService;
    private Image redLightCloudImage;
    private Image redLightImage;
    private ServiceConfResolver serviceConfResolver;

    public HomePage(@Autowired AppProperties appProperties, @Autowired StatusDashboard statusDashboard, @Autowired ResourceUtils resourceUtils, @Autowired PopInService popInService, @Autowired PushService pushService, @Autowired ServicesRepository servicesRepository, @Autowired StatusService statusService, @Autowired NotificationService notificationService, @Autowired ServiceConfResolver serviceConfResolver) {
        this.popInService = popInService;
        this.pushService = pushService;
        this.servicesRepository = servicesRepository;
        this.statusService = statusService;
        this.notificationService = notificationService;
        this.serviceConfResolver = serviceConfResolver;

        var background = new Div();
        background.setClassName("background-image");

        var cloudsImage = new Div();
        cloudsImage.setClassName("clouds-image");

        var rideauDeFer = resourceUtils.getImage(ResourcesPaths.HP_RIDEAU_DE_FER);
        rideauDeFer.setClassName("rideau-de-fer-image");

        var alertLampImage = resourceUtils.getImage(ResourcesPaths.HP_ALERT_LAMP);
        alertLampImage.setClassName("alert-lamp-image");

        var redLightCloudImage = resourceUtils.getImage(ResourcesPaths.HP_ALERT_RED_LIGHT_CLOUD);
        redLightCloudImage.setVisible(false);


        var redLightImage = resourceUtils.getImage(ResourcesPaths.HP_ALERT_RED_LIGHT);
        redLightImage.setVisible(false);

        var divAlarm = new Div();
        divAlarm.setClassName("div-alarm");
        divAlarm.add(alertLampImage, redLightImage, redLightCloudImage);

        this.redLightImage = redLightImage;
        this.redLightCloudImage = redLightCloudImage;

        statusDashboard.setAlertVariables(redLightImage, redLightCloudImage);
        statusDashboard.updateAlertDisplay();

        add(
                rideauDeFer,
                divAlarm,
                background,
                cloudsImage,

                new HeaderComponent(redLightImage, redLightCloudImage),
                new MainHpComponent(
                        new H1("Dashboard Java Status")
                )
                ,statusDashboard
        );
    }

    private void updateAlert() {

    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        pushService.addUI(attachEvent.getUI(), this);
    }

    public void updateDashboard() {
        try {
            getChildren()
                    .filter(component -> component instanceof StatusDashboard)
                    .findFirst()
                    .ifPresentOrElse(component -> {
                        remove(component);

                        var statusDashboard = new StatusDashboard(servicesRepository, statusService, notificationService, popInService, pushService, serviceConfResolver);
                        add(statusDashboard);

                        statusDashboard.setAlertVariables(redLightImage, redLightCloudImage);
                        statusDashboard.updateAlertDisplay();

                        popInService.showPopIn("Dashboard updated", LogLevel.INFO);
                    }, () -> {
                        popInService.showPopIn("Dashboard unfound", LogLevel.ERROR);
                    });
        } catch (Exception e) {
            popInService.showPopIn("Error while updating the Dashboard", LogLevel.ERROR);
        }
    }

    public void displayCustomNotification(String message) {
        popInService.showPopIn(message, LogLevel.TRACE);
    }
}
