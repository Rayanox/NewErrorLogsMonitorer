package com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class LogConsumer {

    private final HttpClient client = HttpClient.newHttpClient();

    public List<String> fetchLogs(ServiceProperties serviceProperties) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(serviceProperties.getLogServers().getUrl()))
                .setHeader("Authorization", "BASIC " + serviceProperties.getLogServers().getBasicAuthToken())
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
