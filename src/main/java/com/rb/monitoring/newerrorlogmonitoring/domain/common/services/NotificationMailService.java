package com.rb.monitoring.newerrorlogmonitoring.domain.common.services;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.Subscribers;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.notifications.MailProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions.ServerDownException;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer.MailConsumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationMailService {

    private static final String KEY_ID_LOG_ENTRY = "{ID_LOG_ENTRY}";

    private final MailConsumer mailConsumer;
    private final MailProperties mailProperties;
    private final AppProperties appProperties;

    public void sendMail(LogEntry logEntry) {
        var urlExposed = appProperties.getRestUrlLogExposed().replace(KEY_ID_LOG_ENTRY, logEntry.getId().toString());
        var content = "Bonjour,\n\n" +
                "Une nouvelle erreur a été détectée sur le service " + logEntry.getService().getServiceName() + " le " + logEntry.getDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)) + ".\n\n" +
                "Voici le message d'erreur : \n" + logEntry.getMessage() + "\n\n" +
                "Consultez les infos complètes ici : " + urlExposed + "\n\n" +
                "Cordialement,\n" +
                "L'équipe Monitoring";

        getDestinations(false).forEach(destination ->
                mailConsumer.sendMail(content, "Nouveau log d'erreur détecté en PREPROD", destination, true)
        );
    }

    public void sendMail(ServerDownException e) {
        getDestinations(false).forEach(destination ->
                mailConsumer.sendMail(e.getMessage(), "Serveur down: " + e.getServiceProps().getServiceName(), destination, true)
        );
    }

    public void sendMail(Exception e) {
        getDestinations(true).forEach(destination ->
                mailConsumer.sendMail(ExceptionUtils.getStackTrace(e), "["+ appProperties.getApplicationName()+"][ADMIN][ERROR] Exception happened in the application service", destination, false)
        );
    }

    /*
        PRIVATES
     */

    private Set<String> getDestinations(boolean admin) {
        if (admin) {
            return new HashSet<String>(mailProperties.getSubscribers().getAdminUsers());
        } else {
            var unionDestination = new HashSet<String>(mailProperties.getSubscribers().getLogUsers());
            unionDestination.addAll(mailProperties.getSubscribers().getAdminUsers());
            return unionDestination;

        }
    }

}
