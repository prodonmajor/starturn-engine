/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.alert;

import com.starturn.engine.util.notification.model.EmailMessage;
import com.starturn.engine.util.notification.model.EmailPlaceholder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Administrator
 */
public class EmailSender {

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    Configuration freemarkerConfig = new Configuration();
    private static final String SMTP_HOST_O365 = "smtp.office365.com";
    private static final int SMTP_PORT_O365 = 587;
    private static final String SMTP_USERNAME_O365 = "ITsupport@myeasycoop.com";
    private static final String SMTP_PASSWORD_O365 = "Renegade@123456";

    /**
     * Sends email to multiple email addresses
     *
     * @param fromUser the sender gmail username without @gmail.com (e.g
     * smith2345)
     * @param fromUserEmailPassword the password of the fromUser
     * @param to_emails the emails to send message to
     * @param subject subject of the email
     * @param emailBody the body of the mail
     * @param template html template to be sent
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendBulkEmail(String fromUser, String fromUserEmailPassword, List<String> to_emails, String subject, String emailBody, String template) throws AddressException, MessagingException {

        String emailPort = "587";//gmail's smtp port

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        for (int i = 0; i < to_emails.size(); i++) {
            emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_emails.get(i)));
        }

        MessageFormat mf = new MessageFormat(template);
        String message_html = mf.format(new Object[]{emailBody});

        emailMessage.setSubject(subject);
        emailMessage.setContent(message_html, "text/html");//for a html email
        //emailMessage.setText(emailBody);// for a text email
        String emailHost = "smtp.gmail.com";

        Transport transport = mailSession.getTransport("smtp");

        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }

    /**
     * Sends email to a single email address
     *
     * @param fromUser the sender gmail username without @gmail.com (e.g
     * smith2345)
     * @param fromUserEmailPassword the password of the fromUser
     * @param to_email the email to send message to
     * @param subject subject of the email
     * @param emailBody the body of the mail
     * @param template html template to be sent
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendSingleEmail(String fromUser, String fromUserEmailPassword, String to_email, String subject, String emailBody, String template) throws AddressException, MessagingException {

        String emailPort = "587";//gmail's smtp port

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_email));

        MessageFormat mf = new MessageFormat(template);
        String message_html = mf.format(new Object[]{emailBody});

        emailMessage.setSubject(subject);
        emailMessage.setContent(message_html, "text/html");//for a html email
        //emailMessage.setText(emailBody);// for a text email
        String emailHost = "smtp.gmail.com";

        Transport transport = mailSession.getTransport("smtp");

        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }

    /**
     * Sends email to a single email address
     *
     * @param to_email the email to send message to
     * @param subject subject of the email
     * @param templateName
     * @param emailMessage
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    public void sendSingleEmailOfiice365(String to_email, String subject, String templateName,
            EmailMessage emailMessage) throws IOException, TemplateException {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME_O365, SMTP_PASSWORD_O365);
            }

        });

        try {
            freemarkerConfig.setClassForTemplateLoading(EmailSender.class, "/templates/");
            //helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
            //URLDataSource source = new URLDataSource(this.getClass().getResource("/templates/logo.png"));
            String defaultTemplate = "general";
            if (templateName == null || templateName.trim().isEmpty()) {
                templateName = defaultTemplate;
            }

            Map<String, Object> model = new AlertUtil().getEmailTemplateModel(templateName, emailMessage);

            Template t = freemarkerConfig.getTemplate(templateName + ".ftl");
            StringWriter writer = new StringWriter();
            t.process(model, writer);

            final Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
            message.setFrom(new InternetAddress(SMTP_USERNAME_O365));
            message.setSubject(subject);
            message.setContent(writer.toString(), "text/html");
            message.setSentDate(new Date());
            Transport.send(message);
            System.out.println("Email sent successfully to - " + to_email);
        } catch (final MessagingException ex) {
            System.out.println("Error occurred:::" + ex);
        }

    }

    /**
     * Sends email to a single email address
     *
     * @param to_email the email to send message to
     * @param subject subject of the email
     * @param body
     * @param firstName
     * @param lastName
     * @param coopName
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    public void sendGeneralEmail(String to_email, String subject, String body, String firstName, String lastName,
            String coopName) throws IOException, TemplateException {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME_O365, SMTP_PASSWORD_O365);
            }

        });

        try {
            freemarkerConfig.setClassForTemplateLoading(EmailSender.class, "/templates/");
            //helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
            //URLDataSource source = new URLDataSource(this.getClass().getResource("/templates/logo.png"));
            String templateName = "general.ftl";
            EmailMessage emailMessage = new EmailMessage();
            
            EmailPlaceholder placeholder = new EmailPlaceholder();
            placeholder.setFirst_name(firstName);
            placeholder.setLast_name(lastName);
            placeholder.setCoop_name(coopName);
            
            emailMessage.setPlaceholder(placeholder);
            emailMessage.setEmailBody(body);

            Map<String, Object> model = new AlertUtil().getEmailTemplateModel(templateName, emailMessage);

            Template t = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            t.process(model, writer);

            final Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
            message.setFrom(new InternetAddress(SMTP_USERNAME_O365));
            message.setSubject(subject);
            message.setContent(writer.toString(), "text/html");
            message.setSentDate(new Date());
            Transport.send(message);
            System.out.println("Email sent successfully to - " + to_email);
        } catch (final MessagingException ex) {
            System.out.println("Error occurred:::" + ex);
        }

    }

    /**
     * Sends email to a single email address
     *
     * @param to_email the email to send message to
     * @param subject subject of the email
     * @param body
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    public void sendGeneralEmailFrontEnd(String to_email, String subject, String body) throws IOException, TemplateException {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME_O365, SMTP_PASSWORD_O365);
            }

        });

        try {
            freemarkerConfig.setClassForTemplateLoading(EmailSender.class, "/templates/");
            //helper.addAttachment("logo.png", new ClassPathResource("logo.png"));
            //URLDataSource source = new URLDataSource(this.getClass().getResource("/templates/logo.png"));
            Map<String, Object> model = new HashMap<>();
            model.put("body", body);

            Template t = freemarkerConfig.getTemplate("generalfrontend.ftl");
            StringWriter writer = new StringWriter();
            t.process(model, writer);

            final Message message = new MimeMessage(session);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_email));
            message.setFrom(new InternetAddress(SMTP_USERNAME_O365));
            message.setSubject(subject);
            message.setContent(writer.toString(), "text/html");
            message.setSentDate(new Date());
            Transport.send(message);
            System.out.println("Email sent successfully to - " + to_email);
        } catch (final MessagingException ex) {
            System.out.println("Error occurred:::" + ex);
        }

    }

    public Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", SMTP_HOST_O365);
        config.put("mail.smtp.port", SMTP_PORT_O365);
        return config;
    }
}

