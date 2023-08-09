package com.rb.monitoring.newerrorlogmonitoring.domain.logger;

import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NewLogValidatorRule {

    private StacktraceUpdateService stacktraceUpdateService;

    /**
     * Validate if the log is new by validating theses rules:
     *  #- the log level must be ERROR and the exception must not be null
     *  #- the exception messages equals
     *  - the exception endOfStacktrace equals
     *  - the exception companyStacktrace equals
     * @param previousLogSaved
     * @param logNew
     * @return true if the log is a new one detected, that has never happen in logs, or false otherwise
     */
    public boolean isNewLog(List<LogEntry> previousLogSaved, LogEntry logNew) {
        if(logNew.getException() == null) {
            return false;
        }

        return previousLogSaved.stream()
                .filter(log -> processEquals(log,logNew))
                .findAny()
                .isEmpty();
    }

    private boolean processEquals(LogEntry previousLogSaved, LogEntry logNew) {
        var areEquals = previousLogSaved.equals(logNew);
        if(areEquals) {
            return true;
        }

        if(checkSureInegalities(previousLogSaved, logNew)) {
            return false;
        }

        if(stacktraceUpdateService.detectStacktraceUpdate(previousLogSaved, logNew, areEquals)) {
            stacktraceUpdateService.processStacktraceUpdate(previousLogSaved, logNew);
            return true;
        }else {
            return false;
        }
    }

    private boolean checkSureInegalities(LogEntry previousLogSaved, LogEntry logNew) {
        return !previousLogSaved.getLogLevel().equals(logNew.getLogLevel())
                || !previousLogSaved.getClassNameLog().equals(logNew.getClassNameLog());
    }
}
