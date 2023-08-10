package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import java.util.Comparator;

public class StatusComparator implements Comparator<StatusEnum> {

    @Override
    public int compare(StatusEnum o1, StatusEnum o2) {
        return Integer.compare(o1.getLevel(), o2.getLevel());
    }
}
