package com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer;

import com.rb.monitoring.newerrorlogmonitoring.domain.common.ServiceProperties;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class LogConsumer {

    //FIXME: integrer dans les properties
    private String urlApi = "http://logs.in.karavel.com/logs/rfo/webpreprod02/var/log/tomcat53-svc-sejour.catalogue/catalogue.sejour.pojo.soap.ws.log";
    private String headerAuthorization = "Basic cmJlbmhtaWRhbmU6S3FWWkZtWTI=";

    private final HttpClient client = HttpClient.newHttpClient();

    public List<String> fetchLogs(ServiceProperties serviceProperties) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(urlApi))
                .setHeader("Authorization", headerAuthorization)
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
