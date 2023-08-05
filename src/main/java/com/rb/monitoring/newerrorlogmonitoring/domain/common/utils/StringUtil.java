package com.rb.monitoring.newerrorlogmonitoring.domain.common.utils;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {


    public static String notEmty(String text) {
        return StringUtils.isBlank(text)
                ? null
                : text;
    }

    public static String removeLastLineReturnIfPresent(String text) {
        return text.endsWith("\n")
                ? text.substring(0, text.length() - 1)
                : text;
    }
}
