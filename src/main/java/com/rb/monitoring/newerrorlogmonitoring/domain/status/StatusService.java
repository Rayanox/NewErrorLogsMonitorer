package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.Environment;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatusService {

    public StatusEnum getStatus(Environment environment) {
        var isErrorStatus = environment.getLogEntries().stream()
                .map(LogEntry::getStatus)
                .filter(this::pertinentLog)
                .anyMatch(status -> status.isHasToBeChecked());

        if(isErrorStatus) {
            return StatusEnum.ERROR_DETECTED;
        }

        return environment.getLogEntries().stream()
                .map(LogEntry::getStatus)
                .filter(this::pertinentLog)
                .findAny()
                .map(status -> StatusEnum.ERROR_CHECKED)
                .orElse(StatusEnum.OK);
    }

    /*
        PRIVATE
     */

    private boolean pertinentLog(Status status) {
        return !status.isFirstIndexation();
    }

}
