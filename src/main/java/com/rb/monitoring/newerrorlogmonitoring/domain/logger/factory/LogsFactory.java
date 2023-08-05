package com.rb.monitoring.newerrorlogmonitoring.domain.logger.factory;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.RegexUtils;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.RegexUtils.extractByRegex;
import static com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.RegexUtils.replaceIdInstance;

public abstract class LogsFactory {

    protected ServiceProperties serviceConf;
    protected AppProperties appProperties;

    protected final Iterator<String> logs;
    protected final List<String> logLinesInput;
    protected int lineIndex = -1;

    protected final List<LogEntry> logEntriesResult = new ArrayList<>();
    protected LogEntry.LogEntryBuilder logEntryBuilder = LogEntry.builder();
    protected String currentLogLine;

    protected boolean isInExceptionProcessing = false;

    public LogsFactory(List<String> logs, ServiceProperties serviceConf, AppProperties appProperties) {
        this.logs = logs.iterator();
        this.logLinesInput = logs;
        this.serviceConf = serviceConf;
        this.appProperties = appProperties;
    }

    public abstract List<LogEntry> process();

    protected static LocalDateTime getDate(String logLine, String patternDate, String dateTimeFormatter, int lineIndex) {
        String date = extractByRegex(logLine, patternDate, lineIndex);
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(dateTimeFormatter));
    }

    protected LogLevel getLogLevel() {
        String level = extractByRegex(currentLogLine, serviceConf.getPatterns().getPatternLogLevel(), lineIndex);
        return LogLevel.valueOf(level.toUpperCase(Locale.getDefault()));
    }

    protected String getClassName() {
        return extractByRegex(currentLogLine, serviceConf.getPatterns().getPatternClassName(), lineIndex);
    }

    protected String getMessage() {
        return extractByRegex(currentLogLine, serviceConf.getPatterns().getPatternMessage(), lineIndex);
    }

    protected String getExceptionMessage(boolean isCausedBy) {
        if(isCausedBy) {
            return extractByRegex(currentLogLine, serviceConf.getPatterns().getPatternCausedByMessage(), lineIndex);
        }else {
            var previousLogLine = logLinesInput.get(lineIndex-1);
            return replaceIdInstance(previousLogLine.trim());
        }
    }

    protected boolean isCausedByLine() {
        return currentLogLine.startsWith("Caused by:");
    }
    protected boolean isStacktraceLine() {
        return RegexUtils.matches(currentLogLine, serviceConf.getPatterns().getPatternStackTraceLine());
    }

    /*
        PRIVATES
     */


}
