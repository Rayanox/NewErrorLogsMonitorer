package com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions;

public class RegexMatchNotFoundException extends RuntimeException {

    public RegexMatchNotFoundException(String pattern, String text, int lineNumber) {
        super("Regex pattern: " + pattern + " not found in text: " + text + ". Line number: " + (lineNumber+1));
    }
}
