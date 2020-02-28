/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.model;

import java.util.List;

/**
 *
 * @author Administrator
 */
public class EmailMessage {

    private String fromUser;
    private String fromUserEmailPassword;
    private List<String> to_emails;
    private String to_email;
    private String subject;
    private String emailBody;
    private String template;
    private EmailPlaceholder placeholder;

    public EmailMessage() {
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromUserEmailPassword() {
        return fromUserEmailPassword;
    }

    public void setFromUserEmailPassword(String fromUserEmailPassword) {
        this.fromUserEmailPassword = fromUserEmailPassword;
    }

    public List<String> getTo_emails() {
        return to_emails;
    }

    public void setTo_emails(List<String> to_emails) {
        this.to_emails = to_emails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTo_email() {
        return to_email;
    }

    public void setTo_email(String to_email) {
        this.to_email = to_email;
    }

    public EmailPlaceholder getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(EmailPlaceholder placeholder) {
        this.placeholder = placeholder;
    }
}
