/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.thread;

import com.starturn.engine.util.notification.alert.EmailSender;
import com.starturn.engine.util.notification.model.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author emmanuel.idoko
 */
@Component
public class EmailAlertHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmailAlertHelper.class);
    EmailSender emailAlert = new EmailSender();

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendBulkEmail(EmailMessage emailMessage) {
        logger.info("request to send single bulk email message to {},", emailMessage.getTo_emails());
        try {
            emailAlert.sendBulkEmail(emailMessage.getFromUser(), emailMessage.getFromUserEmailPassword(), emailMessage.getTo_emails(),
                    emailMessage.getSubject(), emailMessage.getEmailBody(), emailMessage.getTemplate());
            logger.info("finished sending bulk email to {} ", emailMessage.getTo_emails());
        } catch (Exception ex) {
            logger.info("Error sending bulk email message. See error log");
            logger.error("Error sending bulk email message - ", ex);
        }
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendSingleEmail(EmailMessage emailMessage) {
        logger.info("request to send single email message to {},", emailMessage.getTo_emails());
        try {
            emailAlert.sendSingleEmail(emailMessage.getFromUser(), emailMessage.getFromUserEmailPassword(), emailMessage.getTo_email(),
                    emailMessage.getSubject(), emailMessage.getEmailBody(), emailMessage.getTemplate());
            logger.info("finished sending single email to {} ", emailMessage.getTo_email());
        } catch (Exception ex) {
            logger.info("Error sending single email message. See error log");
            logger.error("Error sending single email message - ", ex);
        }
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendSingleEmailOffice365(EmailMessage emailMessage) {
        logger.info("request to send single email message to {},", emailMessage.getTo_emails());
        try {
            emailAlert.sendSingleEmailOfiice365(emailMessage.getTo_email(),
                    emailMessage.getSubject(), emailMessage.getTemplate(), emailMessage);
            logger.info("finished sending single email to {} ", emailMessage.getTo_email());
        } catch (Exception ex) {
            logger.info("Error sending single email message. See error log");
            logger.error("Error sending single email message - ", ex);
        }
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendGeneralEmail(EmailMessage emailMessage) {
        logger.info("request to send general email message to [" + emailMessage.getTo_email() + "]");
        try {
            emailAlert.sendGeneralEmail(emailMessage.getTo_email(),
                    emailMessage.getSubject(), emailMessage.getEmailBody(), emailMessage.getPlaceholder().getFirst_name(),
                    emailMessage.getPlaceholder().getLast_name(), emailMessage.getPlaceholder().getCoop_name());
            logger.info("finished sending general email to [" + emailMessage.getTo_email() + "] ");
        } catch (Exception ex) {
            logger.info("Error sending general email message. See error log");
            logger.error("Error sending general email message - ", ex);
        }
    }
    
     @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendGeneralEmailFrontEnd(EmailMessage emailMessage) {
        logger.info("request to send general email message to [" + emailMessage.getTo_email() + "]");
        try {
            emailAlert.sendGeneralEmailFrontEnd(emailMessage.getTo_email(),
                    emailMessage.getSubject(), emailMessage.getEmailBody());
            logger.info("finished sending general email to [" + emailMessage.getTo_email() + "] ");
        } catch (Exception ex) {
            logger.info("Error sending general email message. See error log");
            logger.error("Error sending general email message - ", ex);
        }
    }
}

