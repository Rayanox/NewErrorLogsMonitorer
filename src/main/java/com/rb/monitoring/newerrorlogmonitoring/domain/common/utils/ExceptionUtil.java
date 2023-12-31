package com.rb.monitoring.newerrorlogmonitoring.domain.common.utils;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.LogPatternProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.ExceptionEntry;

import java.util.Objects;

public class ExceptionUtil {

    /**
     * Returns the exception message of exception or of the first cause that contains a non-null message
     * @param e Exception object which message will be tried to be reached
     * @return The message of the exception or of one of its cause
     */
    public static String getMessageRecursively(Exception e) {
        String message = e.getMessage();

        Throwable exceptionProcessed = e;
        while (Objects.isNull(message) && Objects.nonNull(exceptionProcessed.getCause())) {
            exceptionProcessed = exceptionProcessed.getCause();
            message = exceptionProcessed.getMessage();
        }

        return message;
    }

    public static boolean isNetworkException(ExceptionEntry e, LogPatternProperties patternProperties) {
        return RegexUtils.matches(e.getMessage(), patternProperties.getPatternExceptionMessageErrorNetwork());
    }

}
