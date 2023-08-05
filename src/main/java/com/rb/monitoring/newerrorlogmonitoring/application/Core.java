package com.rb.monitoring.newerrorlogmonitoring.application;

//import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ExceptionService;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.LogService;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.MailConsumer;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer.LogConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class Core {

    private final LogConsumer logConsumer;
    private final LogService logService;
    private final AppProperties appProperties;
    private final MailConsumer mailConsumer;

    public void process() {
        // Retrieve new data for all applications
        // Retrieve database data for all applications
        // Diff between both
        // Notify new diffs detected (Log + Alert)

//        mailConsumer.sendMail(null, null);

//        var oldLogs = logService.readOldLogs();
//
        for (var service : appProperties.getServices()) {
            var logs = logConsumer.fetchLogs(service);
            int i = 0;
            i++;
            //TODO: 1) Lecture DB
            //TODO: 2) Intégrer la diff et la notification
            //TODO: 3) Ecriture DB des nouvelles entrées


//            var logEntries = logService.readLogs(logs, service);
//            var newLogs = logService.diffLogs(logEntries, oldLogs);
//            var diffLogs = logService.ruleNewLogsDetected(newLogs);
//            logService.notifyNewLogsDetected(diffLogs);
        }
    }
}
