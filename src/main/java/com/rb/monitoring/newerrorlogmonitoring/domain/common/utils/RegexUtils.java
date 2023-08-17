package com.rb.monitoring.newerrorlogmonitoring.domain.common.utils;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.RegexMatchNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static List<String> extractGroupsByRegex(String line, String regex) {
        if(StringUtils.isEmpty(line)) {
            return Collections.EMPTY_LIST;
        }

        Matcher matcher = Pattern.compile(regex).matcher(line);

        var groups = new ArrayList<String>();
        try {
            while(matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    groups.add(matcher.group(i));
                }
            }
        } catch (IllegalStateException e) {
            if(e.getMessage().equals("No match found")) {
                throw new RuntimeException("Regex pattern: " + regex + " not found in text: " + line);
            }
            throw e;
        }
        return groups;
    }
}
