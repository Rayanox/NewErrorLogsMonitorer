package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
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
    @Enumerated(EnumType.STRING)
    @NonNull
    private LogLevel logLevel;
    @NonNull
    private LocalDateTime date;
    private Boolean networkError;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "logEntry")
    @OneToOne(cascade = CascadeType.ALL)
    private ExceptionEntry exception;
    @JsonIgnore
    @ManyToOne
    private Service service;

    @Override
    public String toString() {
        var networkErrorText = BooleanUtils.isTrue(this.networkError) ? "[NetworkError] - " : "";
        return networkErrorText + "LogEntry{" +
                "id=" + id +
                ", date=" + date +
                ", service=" + service +
                ", message='" + message + '\'' +
                '}';
    }
}
