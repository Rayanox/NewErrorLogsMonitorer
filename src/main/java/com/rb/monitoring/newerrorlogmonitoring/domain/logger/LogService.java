package com.rb.monitoring.newerrorlogmonitoring.domain.logger;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.factory.LogsFactoryFromStart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogService {

    private final AppProperties appProperties;
    private final NewLogValidatorRule newLogValidatorRule;

    public List<LogEntry> readLogs(List<String> logs, ServiceProperties serviceConf) {
        return LogsFactoryFromStart
                .newFactory(logs, serviceConf, appProperties)
                .process();
    }

    public List<LogEntry> readLogs(List<String> logInputUpdated, LocalDateTime date, ServiceProperties serviceConf) {
        return LogsFactoryFromStart
                .newFactory(logInputUpdated, date, serviceConf, appProperties)
                .process();
    }

    public List<LogEntry> getNewLogs(List<LogEntry> oldLogs, List<LogEntry> newLogs) {
        return newLogs.parallelStream()
                .filter(newLog -> newLogValidatorRule.isNewLog(oldLogs, newLog))
                .collect(Collectors.toList());

        /*
        var newEntriesDebug = newLogs.parallelStream()
                .filter(newLog -> !oldLogs.contains(newLog))
                .collect(Collectors.toList());

        var newEntryFound = oldLogs.contains(newEntriesDebug.get(0));

        return newEntriesDebug;*/

//        return newLogs.parallelStream()
//                .filter(newLog -> !oldLogs.contains(newLog))
//                .collect(Collectors.toList());
    }
}
