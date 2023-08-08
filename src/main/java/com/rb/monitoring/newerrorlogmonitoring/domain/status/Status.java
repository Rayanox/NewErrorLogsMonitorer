package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.Service;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime insertionDate;
    private LocalDateTime lastSeenDate;
    private boolean hasToBeChecked;
    private boolean firstIndexation;

}
