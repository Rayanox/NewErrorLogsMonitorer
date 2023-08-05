package com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Log4j2
@Builder
@Getter
@EqualsAndHashCode(of = {"endOfStacktrace", "companyStacktrace"})
public class ExceptionEntry {

    private static final int DEPTH_LIMIT_EQUALS_CHECK = 3;

    @NonNull private String message;
    @NonNull private String stacktrace;
//    @NonNull private String fullStacktrace;
    @NonNull private String endOfStacktrace;
    private String companyStacktrace;
    private ExceptionEntry cause;

    public String getFullStacktrace() {
        throw new RuntimeException("Not implemented yet (la stack comprenant ses stacks enfants");
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
