package com.rb.monitoring.newerrorlogmonitoring.domain.common.utils;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.RegexMatchNotFoundException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private static final String ID_INSTANCE_REPLACED = "#ID_INSTANCE_REPLACED";

    public static boolean matches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text).find();
    }

    public static String extractByRegex(String line, String regex, int lineNumber) {
        Matcher matcher = Pattern.compile(regex).matcher(line);
        matcher.find();
        try {
            return replaceIdInstance(matcher.group(1));
        } catch (IllegalStateException e) {
            if(e.getMessage().equals("No match found")) {
                throw new RegexMatchNotFoundException(regex, line, lineNumber);
            }
            throw e;
        }
    }

    public static String replaceIdInstance(String text) {
        return text.replaceAll("#\\d+", ID_INSTANCE_REPLACED);
    }

}
