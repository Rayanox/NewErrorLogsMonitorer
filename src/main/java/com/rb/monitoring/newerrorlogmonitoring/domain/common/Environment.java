package com.rb.monitoring.newerrorlogmonitoring.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "isFirstIndexed"})
public class Environment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String environmentName;
    private String prettyEnvironmentName;

    private LocalDateTime firstIndexedDate;
    private LocalDateTime lastReadDate;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<LogEntry> logEntries = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Service service;

    public boolean isFirstIndexed() {
        return firstIndexedDate != null;
    }
}
