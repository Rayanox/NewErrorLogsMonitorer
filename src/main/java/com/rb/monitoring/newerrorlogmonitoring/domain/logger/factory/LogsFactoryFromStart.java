package com.rb.monitoring.newerrorlogmonitoring.domain.logger.factory;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.RegexUtils;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.ExceptionEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.ExceptionUtil.isNetworkException;
import static com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.StringUtil.notEmty;
import static com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.StringUtil.removeLastLineReturnIfPresent;
import static java.util.Optional.ofNullable;

@Log4j2
public class LogsFactoryFromStart extends LogsFactory {

    private static final int END_OF_STACKTRACE_DEPTH = 10;

    private boolean firstStacktraceLine = false;
    private boolean keepSameLine = false;
    private boolean forceExit = false;

    private StringBuilder stacktraceBuilder;
    private StringBuilder fullStacktraceBuilder;
    private StringBuilder companyStacktraceBuilder;

    private LogsFactoryFromStart(List<String> logs, ServiceProperties serviceConf, AppProperties appProperties) {
        super(logs, serviceConf, appProperties);
    }

    public static LogsFactory newFactory(List<String> logs, ServiceProperties serviceConf, AppProperties appProperties) {
        return new LogsFactoryFromStart(logs, serviceConf, appProperties);
    }

    public static LogsFactory newFactory(List<String> logInputUpdated, LocalDateTime startDateExcluded, ServiceProperties serviceConf, AppProperties appProperties) {
        OptionalInt indexStartSubstring = getIndexStartSubstring(logInputUpdated, startDateExcluded, serviceConf);
        if(indexStartSubstring.isEmpty()) {
            return newFactory(logInputUpdated, serviceConf, appProperties);
        }

        List<String> newLogLines = logInputUpdated.subList(indexStartSubstring.getAsInt(), logInputUpdated.size());
        return new LogsFactoryFromStart(newLogLines, serviceConf, appProperties);
    }

    private static OptionalInt getIndexStartSubstring(List<String> logInputUpdated, LocalDateTime startDateExcluded, ServiceProperties serviceConf) {
        int indexStartSubstring = -1;
        boolean waitingForNextLogEntry = false;
        for (String logLine : logInputUpdated) {
            indexStartSubstring++;

            if(!isNewLogEntryLine(logLine, serviceConf.getPatterns().getPatternNewEntry())) {
                continue;
            }

            LocalDateTime logLineDate = getDate(logLine, serviceConf.getPatterns().getPatternDate(), serviceConf.getPatterns().getPatternDateFormatter(), indexStartSubstring);
            if(logLineDate.isAfter(startDateExcluded)) {
                return OptionalInt.of(indexStartSubstring);
            }
        }
        return OptionalInt.empty();
    }

//    public List<LogEntry> process() {
//        while(logs.hasNext()) {
//            getNextLine();
//
//            if(isNewLogEntryLine(currentLogLine, serviceConf.getPatternNewEntry())) {
//                if(!firstLine) {
//                    saveLastLogEntry();
//                    logEntryBuilder = LogEntry.builder();
//                }
//
//                logEntryBuilder.message(getMessage());
//                logEntryBuilder.classNameLog(getClassName());
//                logEntryBuilder.logLevel(getLogLevel());
//                logEntryBuilder.date(getDate());
//            }else {
//                if(isInEmptyRangeLine()) {//FIXME A corriger
////                    processEmptyRangeLine();
//                }else { //Exception line
//                    ExceptionEntry exception = processException(false);
//                    logEntryBuilder.exception(exception);
//                }
//            }
//            firstLine = false;
//        }
//        saveLastLogEntry();
//        return logEntries;
//    }

    public List<LogEntry> process() {
        while(logs.hasNext() && !forceExit) {
            getNextLine();

            if(isNewLogEntryLine(currentLogLine, serviceConf.getPatterns().getPatternNewEntry())) {
                processNewLogEntryLine();
            }else {
                ExceptionEntry exception = processException(false);
                logEntryBuilder.exception(exception);
                logEntryBuilder.networkError(isNetworkException(exception, serviceConf));
                saveLastLogEntry();
            }
        }
        return logEntriesResult;
    }

    private void skipUntilFirstStacktraceLine() {
        while(logs.hasNext()) {
             getNextLine();
            if(isStacktraceLine()) {
                break;
            }
        }
    }

    private void processNewLogEntryLine() {
        boolean firstMessageLine = true;
        List<String> messageLines = new ArrayList<>();

        do {
            if(firstMessageLine) {
                firstMessageLine = false;
                messageLines.add(getMessage());
                logEntryBuilder.classNameLog(getClassName());
                logEntryBuilder.logLevel(getLogLevel());
                logEntryBuilder.date(getDate(currentLogLine, serviceConf.getPatterns().getPatternDate(), serviceConf.getPatterns().getPatternDateFormatter(), lineIndex));
            }else {
                messageLines.add(currentLogLine);
            }
            getNextLine();
        } while(logs.hasNext() && !isStacktraceLine() && !isNewLogEntryLine(currentLogLine, serviceConf.getPatterns().getPatternNewEntry()) && !forceExit);

        if(isNewLogEntryLine(currentLogLine, serviceConf.getPatterns().getPatternNewEntry())) {
            logEntryBuilder.message(String.join("\n", messageLines));
            saveLastLogEntry();
            keepSameLine = true;
            return;
        }

        if(isStacktraceLine()) {
            messageLines.remove(messageLines.size() - 1);
            keepSameLine = true;
            firstStacktraceLine = true;
        }
        logEntryBuilder.message(String.join("\n", messageLines));
    }

    private static boolean isNewLogEntryLine(String logLine, String patternNewEntry) {
        return RegexUtils.matches(logLine, patternNewEntry);
    }

    private void getNextLine() {
        if(!firstStacktraceLine && !keepSameLine) {
            incrementLine();
            throwIFServerDown();
        }
        if(keepSameLine) {
            keepSameLine = false;
        }
    }

    private void throwIFServerDown() {
        ofNullable(serviceConf.getPatterns().getPatternDeadServer())
                .ifPresent(pattern -> {
                    if (RegexUtils.matches(currentLogLine, pattern)) {
                        throw new ServerDownException(serviceConf, serviceConf);
                    }
                });
    }

    private void incrementLine() {
        if(!logs.hasNext()) {
            forceExit = true;
            return;
        }
        currentLogLine = logs.next();
        lineIndex++;
    }

    private ExceptionEntry processException(boolean isCausedBy) {
        ExceptionEntry.ExceptionEntryBuilder builder = ExceptionEntry.builder();
        resetBuilders();

        builder.message(getExceptionMessage(isCausedBy));

        while(!keepSameLine && !isNewLogEntryLine(currentLogLine, serviceConf.getPatterns().getPatternNewEntry())) {
            processStackTrace(builder);
        }
//        builder.fullStacktrace(fullStacktraceBuilder.toString());

        return builder.build();
    }

    private void processStackTrace(ExceptionEntry.ExceptionEntryBuilder builder) {
        if(!logs.hasNext()) {
            setStacktraces(builder);
            keepSameLine = true;
            return;
        }
        getNextLine();
        firstStacktraceLine = false;

        if(isNewLogEntryLine(currentLogLine, serviceConf.getPatterns().getPatternNewEntry())) {
            setStacktraces(builder);
            keepSameLine = true;
            return;
        }else if(isCausedByLine()) {
            setStacktraces(builder);
            resetBuilders();
            builder.cause(processException(true));
        }else {
            enrichBuilders();
        }
    }

    private void resetBuilders() {
        stacktraceBuilder = new StringBuilder();
        fullStacktraceBuilder = new StringBuilder();
        companyStacktraceBuilder = new StringBuilder();
    }

    private void enrichBuilders() {
        var line = currentLogLine.trim();

        stacktraceBuilder.append(line).append("\n");
        fullStacktraceBuilder.append(line).append("\n");
        if(line.contains(appProperties.getCompanyPackage())) {
            companyStacktraceBuilder.append(line).append("\n");
        }
    }

    private void setStacktraces(ExceptionEntry.ExceptionEntryBuilder builder) {
        String stacktrace = removeLastLineReturnIfPresent(stacktraceBuilder.toString());
        String companyStacktrace = removeLastLineReturnIfPresent(companyStacktraceBuilder.toString());

        builder.stacktrace(stacktrace);
        builder.companyStacktrace(notEmty(companyStacktrace));
        builder.endOfStacktrace(getEndOfStacktrace(stacktrace));
    }

    private String getEndOfStacktrace(String stacktrace) {
        String[] stacktraceLines = stacktrace.split("\n");
        StringBuilder endOfStacktrace = new StringBuilder();
        for(int i = 0; i < END_OF_STACKTRACE_DEPTH && i < stacktraceLines.length; i++) {
            endOfStacktrace.append(stacktraceLines[i]).append("\n");
        }
        return endOfStacktrace.toString();
    }

    private void saveLastLogEntry() {
        var newLogEntry = logEntryBuilder.build();
        logEntriesResult.add(newLogEntry);
        logEntryBuilder = LogEntry.builder();

        processNotificationsLogs(newLogEntry);
    }

    private void processNotificationsLogs(LogEntry newLogEntry) {
        var exceptionEntry = newLogEntry.getException();
        if(Objects.nonNull(exceptionEntry)) {
            if(Objects.isNull(exceptionEntry.getCompanyStacktrace())) {
                if(isNetworkException(exceptionEntry, serviceConf)) {
                    log.debug("OK, on a un message d'erreur réseau -> " + exceptionEntry.getMessage());
                } else {
                    log.warn("Unclassified log exception detected -> (no company stacktrace neither network trouble) for log entry ending at line: " + lineIndex + ", at date: " + newLogEntry.getDate() + "and message start: " + StringUtils.abbreviate(newLogEntry.getMessage(), 50));
                    //TODO: Notify admins by mail (add conf in properties)
                }
            }else {
                log.debug("OK, on a une stack company");
            }
        }
    }
}
