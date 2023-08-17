package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.rest;

import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.repositories.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogEntriesRestController {

    private final LogEntryRepository logEntryRepository;

    @GetMapping("/getLogEntry")
    public LogEntry getLogEntry(@RequestParam Long id) {
        return logEntryRepository.findById(id)
                .orElseThrow();
    }

    @GetMapping("/getLogEntries")
    public List<LogEntry> getLogEntries(@RequestParam Long environmentId) {
        return logEntryRepository.findAllByEnvironmentId(environmentId);
    }

    @GetMapping("/getLogEntriesFrom")
    public List<LogEntry> getLogEntries(@RequestParam LocalDateTime date, @RequestParam Long environmentId) {
        return logEntryRepository.findAllByDateGreaterThanEqualAndEnvironmentId(date, environmentId);
    }

}
