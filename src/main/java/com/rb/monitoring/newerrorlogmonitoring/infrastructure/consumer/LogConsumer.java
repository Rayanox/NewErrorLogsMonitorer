package com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class LogConsumer {

    private final HttpClient client = HttpClient.newHttpClient();

    public List<String> fetchLogs(EnvironmentWrapperConfig environmentConf) {
        var logServerProperties = environmentConf.getServiceProperties().getLogServers();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(logServerProperties.getUrl()))
                .setHeader("Authorization", "BASIC " + logServerProperties.getBasicAuthToken())
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching logs", e);
        }
        return response.body().lines().toList();
    }

}
