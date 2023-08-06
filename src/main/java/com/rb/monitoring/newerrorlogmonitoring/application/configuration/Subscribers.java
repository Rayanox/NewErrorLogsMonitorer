package com.rb.monitoring.newerrorlogmonitoring.application.configuration;

import lombok.Data;
import java.util.List;

@Data
public class Subscribers {

    private List<String> adminUsers;
    private List<String> logUsers;
}
