package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Override
    public String toString() {
        var networkErrorText = BooleanUtils.isTrue(this.networkError) ? "[NetworkError] - " : "";

        StringBuilder builder = new StringBuilder(networkErrorText);
        builder.append("LogEntry{")
                .append("id=").append(id)
                .append(", date=").append(date);
        if (environment != null) {
            builder.append(", environment=").append(environment);
            if (environment.getService() != null) {
                builder.append(", service=").append(environment.getService().getServiceName());
            }
        }
        builder.append(", message='").append(message).append('\'')
                .append('}');
        return builder.toString();
    }
}
