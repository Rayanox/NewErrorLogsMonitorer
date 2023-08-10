package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusComparator;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusEnum;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
        var rowDtos = buildRows(services);
        add(
                buildTreeGrid(rowDtos)
        );
        logStatus(services);
    }

    private TreeGrid<RowDto> buildTreeGrid(Collection<RowDto> environmentsRowDtos) {
        var treeGrid = new TreeGrid<RowDto>();
        treeGrid.setItems(environmentsRowDtos, (env) -> env.getServiceRows());
        treeGrid.addHierarchyColumn(RowDto::getName)
                .setHeader("Environments/Services");

        treeGrid.addComponentColumn(row -> row.getColoredStatus().getComponent())
                .setHeader("Status");

        treeGrid.addComponentColumn(RowDto::getErrorDataLinkButton).setHeader("Logs");
        treeGrid.addComponentColumn(RowDto::getCheckStatusButton).setHeader("Check");
        treeGrid.addComponentColumn(RowDto::getForceResetButton).setHeader("Reset");

        return treeGrid;
    }

    private Collection<RowDto> buildRows(List<Service> services) {
        var result = new HashMap<String, RowDto>();

        for (Service service : services) {
            for (Environment environment : service.getEnvironments()) {
                var serviceRow = RowDto.builder()
                        .name(service.getServiceName())
                        .isServiceRow(true)
                        .serviceRows(new ArrayList<>())
                        .environmentServiceData(environment)
                        .coloredStatus(getBadgeFromEnvironment(environment))
                        .errorDataLinkButton(buildDataLinkButton(environment))
                        .checkStatusButton(buildCheckStatusButton(environment))
                        .forceResetButton(buildForceResetButton(environment))
                        .build();

                if (result.containsKey(environment.getEnvironmentName())) {
                    result.get(environment.getEnvironmentName()).getServiceRows().add(serviceRow);
                } else {
                    var environmentRow = RowDto.builder()
                            .name(environment.getPrettyEnvironmentName())
                            .isEnvironmentRow(true)
                            .serviceRows(new ArrayList<>())
                            .build();
                    environmentRow.getServiceRows().add(serviceRow);
                    result.put(environment.getEnvironmentName(), environmentRow);
                }
            }
        }
        return result.values()
                .stream()
                .peek(this::setStatus)
                .collect(Collectors.toList());
    }

    private void setStatus(RowDto rowDto) {
        if(rowDto.isServiceRow()) {
            return;
        }

        var environmentStatus = rowDto.getServiceRows().stream()
                .map(row -> row.getColoredStatus().getStatus())
                .max(new StatusComparator());

        rowDto.setColoredStatus(
                environmentStatus.map(status -> StatusDto.builder()
                        .status(status)
                        .component(getBadgeFromStatus(status))
                        .build())
                        .orElse(StatusDto.builder()
                                .status(StatusEnum.UNKNOWN)
                                .component(new Span("UNKNOWN"))
                                .build())
        );
    }

    private Button buildCheckStatusButton(Environment environment) {
        var button = new Button("Check");
        button.addClickListener(event -> {
//            statusService.checkStatus(environment);
            //TODO: To code the checkStatus method
            log.info("TODO: To code the checkStatus method");
            showNotImplementedPopin();
        });
        return button;
    }

    private Button buildForceResetButton(Environment environment) {
        var button = new Button("Reset");
        button.addClickListener(event -> {
//            statusService.forceReset(environment);
            //TODO: To code the forceReset method
            log.info("TODO: To code the forceReset method");
            showNotImplementedPopin();
        });
        return button;
    }


    private void processOpenLogsClick(Environment environment, String dateLogsFrom) {
        if(dateLogsFrom != null) {
            getUI().ifPresent(ui -> ui.getPage().open("/logs/getLogEntriesFrom?date=" + dateLogsFrom, "Logs " + dateLogsFrom));
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

    public StatusDto getBadgeFromEnvironment(Environment environment) {
        var status = statusService.getStatus(environment);
        var span = getBadgeFromStatus(status);

        return StatusDto.builder()
                .component(span)
                .status(status)
                .build();
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

    @Data
    @Builder
    @AllArgsConstructor
    public static class RowDto {
        private String name;
        private Environment environmentServiceData;

        private StatusDto coloredStatus;
        private Button errorDataLinkButton;
        private Button checkStatusButton;
        private Button forceResetButton;

        private boolean isEnvironmentRow;
        private boolean isServiceRow;

        private Collection<RowDto> serviceRows;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class StatusDto {
        private Span component;
        private StatusEnum status;
    }

    private Button buildDataLinkButton(Environment environment) {
        var dateLogsFrom = getDateFirstUncheckedError(environment);
        if(dateLogsFrom != null) {
            return new Button("Click to open logs",
                    (event) -> processOpenLogsClick(environment, dateLogsFrom)
            );
        }
        return null;
    }

    private void showNotImplementedPopin() {
        Notification notification = Notification.show("Not implemented yet");
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

}
