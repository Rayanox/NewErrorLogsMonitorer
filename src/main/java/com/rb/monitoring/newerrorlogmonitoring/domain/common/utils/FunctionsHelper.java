package com.rb.monitoring.newerrorlogmonitoring.domain.common.utils;

import java.util.function.Consumer;

public class FunctionsHelper {

    public static <T> Consumer<T> EMPTY_ACTION() {
        return t -> {};
    }

}
