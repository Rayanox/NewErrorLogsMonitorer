package com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions;

import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class UnclassifiedLogException extends RuntimeException {

    public UnclassifiedLogException(LogEntry newLogEntry, int lineIndex) {
        super(buildMessage(newLogEntry, lineIndex));
    }

    private static String buildMessage(LogEntry newLogEntry, int lineIndex) {
        var message = "Unclassified log exception detected, probably a network error -> (no company stacktrace neither network trouble) for log entry ending at line: " + lineIndex + ", at date: " + newLogEntry.getDate() + "and message start: " + StringUtils.abbreviate(newLogEntry.getMessage(), 50);
        return message + "\n\n"
        + newLogEntry.toString();
    }
}
