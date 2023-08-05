package com.rb.monitoring.newerrorlogmonitoring.application;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Project {
    MEDIATION_MOBILE("mediation.front-office.mobile.bus.ws", "", "var/log/tomcat73-svc-mediation-mobile/mediation.front-office.mobile.bus.log", ServiceType.WS),
    SOCLE_COMMUN("mobile.webapp","mobile.webapp.multiVDD", null, ServiceType.WEBAPP);

    private final String appName;
    private final String jenkinsBuildName;
    private final String logPath;
    private final ServiceType serviceType;

    public static Project getFrom(String appName) {
        return Arrays.stream(values())
                .filter(project -> project.appName.equals(appName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Project enum entry matching '"+ appName +"'"));
    }

}
