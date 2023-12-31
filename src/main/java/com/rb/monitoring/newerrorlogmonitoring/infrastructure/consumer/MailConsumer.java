package com.rb.monitoring.newerrorlogmonitoring.infrastructure.consumer;

import com.rb.monitoring.newerrorlogmonitoring.application.EnvironmentService;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.notifications.MailProperties;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailConsumer {

    private JavaMailSender emailSender;
    private MailProperties mailProperties;
    private EnvironmentService environmentService; //Solution to mock without mockito -> Maybe find an other solution or use mockito later

    public void sendMail(String content, String subject, String destination, boolean useDefaultSubjectHeader) {
        if(environmentService.isTestEnvironment()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(destination);
        if (useDefaultSubjectHeader) {
            message.setSubject(mailProperties.getSubjectHeader() + " " + subject);
        } else {
            message.setSubject(subject);
        }
        message.setText(content);
        emailSender.send(message);
    }

}
