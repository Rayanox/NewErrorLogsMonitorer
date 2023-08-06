package com.rb.monitoring.newerrorlogmonitoring.domain.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime firstIndexationDate;

    private LocalDateTime lastProcessedDate;

    @Transient
    private LocalDateTime nextProcessingDate;

    public boolean isFirstIndexed() {
        return firstIndexationDate != null;
    }
}
