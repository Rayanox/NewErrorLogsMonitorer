package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(of = {"exception"})
public class LogEntry {

    @NonNull private String message;
    @NonNull private String classNameLog;
    @NonNull private LogLevel logLevel;
    @NonNull private LocalDateTime date;

    private ExceptionEntry exception;

}
