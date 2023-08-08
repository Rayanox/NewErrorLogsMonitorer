package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusEnum;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@RouteScope
@SpringComponent
public class StatusDashboard extends VerticalLayout {

    private final StatusService statusService;
    private final NotificationService notificationService;


    public StatusDashboard(ServicesRepository servicesRepository, StatusService statusService, NotificationService notificationService) {
        this.statusService = statusService;
        this.notificationService = notificationService;

        List<Service> services = servicesRepository.findAll();
        List<RowDto> rowDtos = buildRows(services);
        add(
                buildGrid(rowDtos)
        );
        logStatus(services);
    }

    private Grid<RowDto> buildGrid(List<RowDto> rowDtos) {
        var grid = new Grid<RowDto>();

        grid.setItems(rowDtos);

        grid.addColumn(RowDto::getServiceName).setHeader("Service");

        grid.addColumn(RowDto::getEnvironmentName).setHeader("Environnement");

        grid.addComponentColumn(RowDto::getColoredStatus)
                .setHeader("Status");

        grid.addComponentColumn(RowDto::getErrorDataLinkButton)
                .setHeader("Error Data");

        grid.addComponentColumn(rowDto -> {
            Button button = new Button("Check");
            button.addClickListener(event -> {
                log.info("appui sur le bouton reset");
            });
            return button;
        }).setHeader("Check Alert");

        return grid;
    }

    private List<RowDto> buildRows(List<Service> services) {
        return services.stream()
                .flatMap(service -> service.getEnvironments().stream()
                        .map(environment -> RowDto.builder()
                                .serviceName(service.getServiceName())
                                .environment(environment)
                                .coloredStatus(getBadgeFromEnvironment(environment))
                                .errorDataLinkButton(
                                        new Button("Click to open logs",
                                                (event) ->  processOpenLogsClick(environment)
                                        )
                                )
                                .environmentName(environment.getPrettyEnvironmentName())
                                .build()
                        )
                )
                .toList();
    }

    private void processOpenLogsClick(Environment environment) {
        var dateLogsFrom = getDateFirstUncheckedError(environment);
        if(dateLogsFrom != null) {
            getUI().ifPresent(ui -> ui.getPage().open("/getLogEntriesFrom?date=" + dateLogsFrom, "Logs " + dateLogsFrom));
        }else {
//            notificationService.notifyAdminCustom("No error found while deducting date after clicking on environment button", LogLevel.WARN);
            log.warn("No error found while deducting date after clicking on environment button");
        }
    }

    private String getDateFirstUncheckedError(Environment environment) {
        return environment.getLogEntries().stream()
                .filter(logEntry -> logEntry.getStatus().isHasToBeChecked())
                .map(LogEntry::getDate)
                .map(LocalDateTime::toString)
                .findFirst()
                .orElse(null);
    }

    private void logStatus(Iterable<com.rb.monitoring.newerrorlogmonitoring.domain.common.Service> services) {
        services.forEach(service -> {
            log.info("Service name: {}", service.getServiceName());

            service.getEnvironments().forEach(environment -> {
                var status = statusService.getStatus(environment);
                log.info("Environment name: {}, status: {}", environment.getEnvironmentName(), status);
            });
        });
    }

    public Span getBadgeFromEnvironment(Environment environment) {
        return getBadgeFromStatus(statusService.getStatus(environment));
    }

    private Span getBadgeFromStatus(StatusEnum status) {
        Span span;
        switch (status) {
            case OK:
                span = new Span("OK");
                span.getElement().getThemeList().add("badge success");
                break;
            case ERROR_DETECTED:
                span = new Span("Error");
                span.getElement().getThemeList().add("badge error");
                break;
            case ERROR_CHECKED:
                span = new Span("Checked");
                span.getElement().getThemeList().add("badge");
                break;
            default:
                notificationService.notifyAdminCustom("Unknown status of badge for status: " + status, LogLevel.ERROR);
                span = new Span("?");
                span.getElement().getThemeList().add("badge contrast");
        }
        return span;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RowDto {
        private Environment environment;

        private String serviceName;
        private String environmentName;
        private Span coloredStatus;
        private Button errorDataLinkButton;
        private Button checkStatusButton;
        private Button forceResetButton;
    }

}
