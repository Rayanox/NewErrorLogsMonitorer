package com.rb.monitoring.newerrorlogmonitoring.domain.common;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ServiceName")
    private String serviceName;
    private LocalDateTime lastReadDate;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LogEntry> logEntries = new ArrayList<>();

    public static Service newService(ServiceProperties serviceProperties) {
        return new Service(null, serviceProperties.getServiceName(), null, new ArrayList<>());
    }

}
