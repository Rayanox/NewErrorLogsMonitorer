package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.status.Status;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"exception"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonPropertyOrder({"id","date", "message", "classNameLog", "logLevel", "envInfos"})
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @NonNull
    private String message;
    @NonNull
    private String classNameLog;
    @NonNull
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;
    @NonNull
    private LocalDateTime date;
    @OneToOne(cascade = CascadeType.ALL)
    private Status status;
    private Boolean networkError;



//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "logEntry")
    @OneToOne(cascade = CascadeType.ALL)
    private ExceptionEntry exception;
    @JsonIgnore
    @ManyToOne(targetEntity = Environment.class, fetch = FetchType.LAZY)
    private Environment environment;

    public String getEnvInfos() {
        if (environment != null) {
            return String.format("Service(Environment): %s (%s)", environment.getService().getServiceName(), environment.getPrettyEnvironmentName());
        }
        return null;
    }

    @Override
    public String toString() {
        var networkErrorText = BooleanUtils.isTrue(this.networkError) ? "[NetworkError] - " : "";

        StringBuilder builder = new StringBuilder(networkErrorText);
        builder.append("LogEntry{")
                .append("id=").append(id)
                .append(", date=").append(date);
        if (environment != null) {
            builder.append(", environment=").append(environment.getEnvironmentName());
            if (environment.getService() != null) {
                builder.append(", service=").append(environment.getService().getServiceName());
            }
        }
        builder.append(", message='").append(message).append('\'')
                .append('}');
        return builder.toString();
    }
}
