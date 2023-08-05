package com.rb.monitoring.newerrorlogmonitoring.infrastructure;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceProperties;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailConsumer {

    private JavaMailSender emailSender;

    public void sendMail(ServiceProperties service, LogEntry logEntry) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("monitoring-log-preprod@karavel.com");
        message.setTo("rbenhmidane@karavel.com");
        message.setSubject("TODO");
        message.setText("Test");
        emailSender.send(message);
    }
}
