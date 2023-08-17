package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfResolver;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusComparator;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusEnum;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.StatusService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PopInService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common.PushService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.ServicesRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
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
import java.util.*;
import java.util.stream.Collectors;

//TODO: Rajouter bouton Synchro (re-synchro des logs)
@Log4j2
@RouteScope
@SpringComponent
public class StatusDashboard extends VerticalLayout {

    private final StatusService statusService;
    private final NotificationService notificationService;
    private final PopInService popInService;
    private final PushService pushService;
    private final ServiceConfResolver serviceConfResolver;

    private Image redLightImage;
    private Image redLightCloudImage;

    private boolean isErrorStatus = false;


    public StatusDashboard(ServicesRepository servicesRepository, StatusService statusService, NotificationService notificationService, PopInService popInService, PushService pushService, ServiceConfResolver serviceConfResolver) {
        this.statusService = statusService;
        this.notificationService = notificationService;
        this.popInService = popInService;
        this.pushService = pushService;
        this.serviceConfResolver = serviceConfResolver;

        List<Service> services = servicesRepository.findAll();
        var rowDtos = buildRows(services);
        var gridDiv = new Div(buildTreeGrid(rowDtos));
        gridDiv.setClassName("gridDiv");

        add(
                gridDiv
        );
        logStatus(services);
    }

    public void setAlertVariables(Image redLightImage, Image redLightCloudImage) {
        this.redLightImage = redLightImage;
        this.redLightCloudImage = redLightCloudImage;
    }

    public void updateAlertDisplay() {
        if(isErrorStatus) {
            triggerAlert(true);
        } else {
            triggerAlert(false);
        }
    }

    private void triggerAlert(boolean activate) {
        if(!activate) {
            redLightImage.setClassName("");
            redLightImage.setVisible(false);
            redLightCloudImage.setClassName("");
            redLightCloudImage.setVisible(false);
        } else {
            redLightImage.setClassName("red-light-image");
            redLightImage.setVisible(true);
            redLightCloudImage.setClassName("red-light-cloud-image");
            redLightCloudImage.setVisible(true);
        }
    }

    private TreeGrid<RowDto> buildTreeGrid(Collection<RowDto> environmentsRowDtos) {
        var treeGrid = new TreeGrid<RowDto>();
        treeGrid.setItems(environmentsRowDtos, (env) -> env.getServiceRows());
        treeGrid.addHierarchyColumn(RowDto::getName)
                .setHeader("Environments/Services");

        treeGrid.addComponentColumn(row -> row.getColoredStatus().getComponent())
                .setHeader("Status");

        treeGrid.addComponentColumn(RowDto::getSquad).setHeader("Squad");
        treeGrid.addComponentColumn(RowDto::getErrorDataLinkButton).setHeader("Logs");
        treeGrid.addComponentColumn(RowDto::getCheckStatusButton).setHeader("Check");
        treeGrid.addComponentColumn(RowDto::getResetIndexSpan).setHeader("Index/Reset");

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
                        .resetIndexSpan(buildResetIndexSpan(environment))
                        .build();

                if (result.containsKey(environment.getEnvironmentName())) {
                    result.get(environment.getEnvironmentName()).getServiceRows().add(serviceRow);
                } else {
                    var environmentRow = RowDto.builder()
                            .name(environment.getPrettyEnvironmentName())
                            .squad(buildSquadText(environment))
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

    private Span buildSquadText(Environment environment) {
        var envConf = serviceConfResolver.getEnvironmentServiceConfig(environment);
        var squad = envConf.getEnvironmentProperties().getSquad();
        var text = new Span(squad);
        text.getStyle().set("color", "blueviolet");
        return text;
    }

//    private String buildName(Environment environment) {
//        String name = environment.getPrettyEnvironmentName();
//
//        var envConf = serviceConfResolver.getEnvironmentServiceConfig(environment);
//        var squad = envConf.getEnvironmentProperties().getSquad();
//
//        if(squad != null) {
////            Tooltip tooltip = Tooltip.forComponent(name)
////                    .withText("Squad ")
////                    .withPosition(Tooltip.TooltipPosition.TOP_START);
//
//            name += " - " + squad;
//        }
//
//        return name;
//    }

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
                        .component(getIconFromStatus(status, null))
                        .build())
                        .orElse(StatusDto.builder()
                                .status(StatusEnum.UNKNOWN)
                                .component(getUnknownIcon())
                                .build())
        );
    }

    private Icon getUnknownIcon() {
        var icon = createIcon(VaadinIcon.QUESTION, "Unknown");
        icon.getElement().getThemeList().add("badge primary");
        return icon;
    }

    private Span buildCheckStatusButton(Environment environment) {
        var button = new Button("Check");
        button.addClickListener(event -> {
            try {
                statusService.checkLogEntriesOfEnvironment(environment);
//                button.setVisible(false);
                pushService.updateDashboard();
            } catch (Exception e) {
                var errorMessage = String.format("Error while checking log entries for appli %s, environment %s", environment.getService().getServiceName(), environment.getPrettyEnvironmentName());
                var exception = new Exception(errorMessage, e);
                notificationService.notifySubscribers(exception);
                popInService.showPopIn(errorMessage, LogLevel.ERROR);
            }
        });
        var isErrorStatus = environment.getLogEntries().stream()
                .filter(logEntry -> !logEntry.getStatus().isPersistentIndexed())
                .anyMatch(logEntry -> !logEntry.getStatus().isChecked());

        if(!isErrorStatus) {
            button.setVisible(false);
        }

        Span span = new Span(button);
        addToolTip(span, "Checks the error log entries so that they will be in a pending state for hours to see if these new errors appears again or not on the environment. If they don't appear anymore, they will be deleted from the dashboard and the status will go back to OK, otherwise it will return in an alert/error state");

        return span;
    }

    private Span buildResetIndexSpan(Environment environment) {
        var indexButton = new Button("Index", VaadinIcon.PLUS_CIRCLE_O.create());
        indexButton.addClickListener(event -> {
            statusService.index(environment);
            popInService.showPopIn("Index done", LogLevel.INFO);
            pushService.updateDashboard();
        });

        addToolTip(indexButton, "Force the indexaction of all the new log entries that are pending as errors log entries. Those errors will be indexed, so they will be saved as normal logs (not errors)");

        var resetButton = new Button("Reset", VaadinIcon.REFRESH.create());
        resetButton.addClickListener(event -> {
            statusService.reset(environment);
            popInService.showPopIn("Reset done", LogLevel.INFO);
            pushService.updateDashboard();
        });

        addToolTip(resetButton, "Reset all the log entries of the environment -> deletes all the log entries of the environment. A new indexation will be done at next cron occurrence");


        Span span = !StatusEnum.OK.equals(statusService.getStatus(environment))
                ? new Span(indexButton, resetButton)
                : new Span(resetButton);
        return span;
    }

    private void addToolTip(Component component, String text) {
        Tooltip tooltip = Tooltip.forComponent(component)
                .withText(text)
                .withPosition(Tooltip.TooltipPosition.TOP_START);
    }




    private void processOpenLogsClick(Environment environment, String dateLogsFrom) {
        if(dateLogsFrom != null) {
            getUI().ifPresent(ui -> ui.getPage().open("/logs/getLogEntriesFrom?date=" + dateLogsFrom + "&environmentId=" + environment.getId(), "Logs " + dateLogsFrom));
        }else {
//            notificationService.notifyAdminCustom("No error found while deducting date after clicking on environment button", LogLevel.WARN);
            log.warn("No error found while deducting date after clicking on environment button");
        }
    }

    private String getDateFirstUncheckedError(Environment environment) {
        return environment.getLogEntries().stream()
                .filter(logEntry -> !logEntry.getStatus().isPersistentIndexed())
                .filter(logEntry -> !logEntry.getStatus().isChecked())
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
        var span = getIconFromStatus(status, environment);

        return StatusDto.builder()
                .component(span)
                .status(status)
                .build();
    }

    private Icon createIcon(VaadinIcon vaadinIcon, String label) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        // Accessible label
        icon.getElement().setAttribute("aria-label", label);
        // Tooltip
        icon.getElement().setAttribute("title", label);
        return icon;
    }

    private Icon getIconFromStatus(StatusEnum status, Environment environment) {
        Icon icon;
        switch (status) {
            case OK:
                icon = createIcon(VaadinIcon.CHECK, "OK: no new errors detected");
//                icon.getElement().getThemeList().add("badge success");
                icon.getStyle().set("color", "var(--lumo-success-color)");
                break;
            case ERROR_DETECTED:
                isErrorStatus = true;
                icon = createIcon(VaadinIcon.CLOSE_SMALL, "Errors detected: please check logs, fix its and click on the check button. Click the reset button if the errors detected are normal errors");
//                icon.getElement().getThemeList().add("badge error");
                icon.getStyle().set("color", "var(--lumo-error-color)");
                break;
            case ERROR_CHECKED:
                var alt = Objects.nonNull(environment) ? "Checked error: waiting for checked errors to not occur anymore during " + statusService.getUnseenDurationCleanerHours(environment) + " hours." : "Checked: waiting for error not to happen anymore";
                icon = createIcon(VaadinIcon.CLOCK, alt);
//                icon.getElement().getThemeList().add("badge");
                icon.getStyle().set("color", "var(--lumo-primary-color)");
                break;
            default:
                notificationService.notifyAdminCustom("Unknown status of badge for status: " + status, LogLevel.ERROR);
                icon = createIcon(VaadinIcon.CLOSE_SMALL, "Unknown");
//                icon.getElement().getThemeList().add("badge contrast");
                icon.getStyle().set("color", "white");
                break;
        }
        return icon;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class RowDto {
        private String name;
        private Environment environmentServiceData;

        private Span squad;
        private StatusDto coloredStatus;
        private Button errorDataLinkButton;
        private Span checkStatusButton;
        private Span resetIndexSpan;

        private boolean isEnvironmentRow;
        private boolean isServiceRow;

        private Collection<RowDto> serviceRows;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class StatusDto {
        private Icon component;
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

}
