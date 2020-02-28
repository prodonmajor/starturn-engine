/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.thread;

import com.starturn.engine.util.notification.alert.TextAlert;
import com.starturn.engine.util.notification.model.MessageDetails;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author emmanuel.idoko
 */
@Component
public class SmsAlertHelper {

    private static final Logger logger = LoggerFactory.getLogger(SmsAlertHelper.class);

 
    TextAlert textAlert = new TextAlert();

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendSingleTextMessage(MessageDetails message) {
        logger.info("request to send single text message to {},", message.getTo());
        try {
            textAlert.sendSingleText(message);
            logger.info("finished sending message to {} ", message.getTo());
        } catch (Exception ex) {
            logger.info("Error sending single text message to the number {}. See error log - [{}]", message.getTo());
            logger.error("Error sending single text message to the number - ", ex);
        }
    }

    @Async("threadPoolTaskExecutor")
    @EventListener
    public void sendBulkTextMessage(List<MessageDetails> messages) {
        logger.info("request to send bulk text messages.");
        try {
            int index = 0;
            for (MessageDetails md : messages) {
                index++;
                textAlert.sendSingleText(md);
                logger.info("sent message to phone number {} at index {}.", md.getTo(), index);
            }
            logger.info("finished sending bulk text messages");
        } catch (Exception ex) {
            logger.info("Error sending bulk text messages. See error log");
            logger.error("Error sending bulk text messages - ", ex);
        }
    }
}

