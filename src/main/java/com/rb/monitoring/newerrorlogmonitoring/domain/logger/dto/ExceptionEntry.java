package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Log4j2
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"endOfStacktrace", "companyStacktrace"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "fullCompanyStacktrace", "depth", "fullStacktrace"})
public class ExceptionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private static final int DEPTH_LIMIT_EQUALS_CHECK = 3;

    @Lob
    @NonNull
    private String message;
    @Lob
    private String companyStacktrace;
    @Lob
    @NonNull
    private String stacktrace;
    @Lob
    @NonNull
    @JsonIgnore
    private String endOfStacktrace;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ExceptionEntry cause;

    //TODO: Mettre @JsonIgnore dès que possible
    //@JsonIgnore
//    @OneToOne(fetch = FetchType.LAZY)
//    private LogEntry logEntry;

    public String getFullStacktrace() {
        return Objects.isNull(cause)
                ? stacktrace
                : stacktrace + "\n\tCaused by: " + cause.getMessage() + "\n" + cause.getFullStacktrace();
    }

    public String getFullCompanyStacktrace() {
        return getFullCompanyStacktrace(0);
    }

    private String getFullCompanyStacktrace(int depth) {
        String fullStack = "";
        if(Objects.nonNull(cause)) {
            fullStack += cause.getFullCompanyStacktrace(depth + 1);
        }
        if(StringUtils.isNotBlank(companyStacktrace)) {
            if(!fullStack.isEmpty()) {
                fullStack += "...\n";
            }
            fullStack += companyStacktrace;
        }
        if(isFirst(depth) && fullStack.isEmpty()) {
            log.warn("FullStack is null for exception: " + StringUtils.abbreviate(message, 50));
            return null;
        }
        return fullStack;
    }

    private boolean isFirst(int depth) {
        return depth == 0;
    }

    public int getDepth() {
        return Objects.nonNull(cause)
                ? cause.getDepth() + 1
                : 1;
    }

}
