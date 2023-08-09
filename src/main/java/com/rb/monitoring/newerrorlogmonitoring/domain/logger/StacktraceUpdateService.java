package com.rb.monitoring.newerrorlogmonitoring.domain.logger;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.services.NotificationService;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StacktraceUpdateService {

    @Value("${app.logs.stacktrace-pattern-version}")
    private String PATTERN_VERSION;
    @Value("${app.logs.stacktrace-pattern-line-number}")
    private String PATTERN_LINE_NUMBER;

    private static final String REMPLACEMENT_VERSION = "VERSION_NUMBER_REMPLACED";
    private static final String REMPLACEMENT_LINE_NUMBER = ":LINE_NUMBER_REPLACED";

    private static final boolean STACKS_ARE_EQUALS = true;
    private static final boolean STACKS_ARE_DIFFERENT = false;

    private final NotificationService notificationService;



    public boolean detectStacktraceUpdate(LogEntry previousLogSaved, LogEntry logNew, boolean areStrictlyEquals) {
        if(areStrictlyEquals) {
            return STACKS_ARE_EQUALS;
        }

        var exceptionSaved = previousLogSaved.getException();
        var exceptionDetected = logNew.getException();
        if(exceptionSaved == null || exceptionDetected == null) {
            return areStrictlyEquals;
        }
        return areUnifiedEquals(exceptionSaved.getEndOfStacktrace(), exceptionDetected.getEndOfStacktrace())
                && areUnifiedEquals(exceptionSaved.getCompanyStacktrace(), exceptionDetected.getCompanyStacktrace());
    }

    private boolean areUnifiedEquals(String previousStacktrace, String newStacktrace) {
        var previousStacktraceSimplified = previousStacktrace.replaceAll(PATTERN_VERSION, REMPLACEMENT_VERSION).replaceAll(PATTERN_LINE_NUMBER, REMPLACEMENT_LINE_NUMBER);
        var newStacktraceSimplified = newStacktrace.replaceAll(PATTERN_VERSION, REMPLACEMENT_VERSION).replaceAll(PATTERN_LINE_NUMBER, REMPLACEMENT_LINE_NUMBER);

        var updateDetected = previousStacktraceSimplified.equals(newStacktraceSimplified);
        if(updateDetected) {
            return STACKS_ARE_EQUALS;
        }else {
            return STACKS_ARE_DIFFERENT;
        }
    }

    public void processStacktraceUpdate(LogEntry logEntrySaved, LogEntry newLogEntryDetected) {
        var exceptionSaved = logEntrySaved.getException();
        exceptionSaved.setStacktrace(newLogEntryDetected.getException().getStacktrace());
        exceptionSaved.setCompanyStacktrace(newLogEntryDetected.getException().getCompanyStacktrace());
        exceptionSaved.setEndOfStacktrace(newLogEntryDetected.getException().getEndOfStacktrace());
        notificationService.notifyAdminCustom("Stacktrace updated for exception with ID = " + exceptionSaved.getId(), LogLevel.INFO);
    }
}
