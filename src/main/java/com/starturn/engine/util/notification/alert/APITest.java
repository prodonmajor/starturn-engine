/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.alert;

import com.starturn.engine.util.notification.model.EmailPlaceholder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Administrator
 */
public class APITest {
    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    Configuration freemarkerConfig = new Configuration();
    private static final String SMTP_HOST_O365 = "smtp.office365.com";
    private static final int SMTP_PORT_O365 = 587;
    private static final String SMTP_USERNAME_O365 = "ITsupport@myeasycoop.com";
    private static final String SMTP_PASSWORD_O365 = "Renegade@123456";

    public static void main(String[] args) throws MessagingException, IOException, TemplateException {

//        Authentication auth = new Authentication();
//       // auth.setUsername("emmanuel.idoko");
//        //auth.setPassword("Password@1");
//        MessageDetails msg = new MessageDetails();
//
//        msg.setFrom("emmanuel.idoko@sd1.emailtesthub.com");
//        msg.setText("This is just a test email from exchange point email API.");
//        msg.setTo("emmytoonaiz@gmail.com");
//        msg.setSubject("Texting plain email.");
//
//        new EmailResource().sendSingleEmail(auth, msg);
//        Authentication auth = new Authentication();
//        auth.setUsername("emmanuel.idoko");
//        auth.setPassword("Password@1");
//        MessageDetails msg = new MessageDetails();
//        msg.setFrom("INFOSMS");
//        msg.setMemberName("Akinwale Agbaje");
//        msg.setText("Dear " + msg.getMemberName() + ",This is just a test message from exchange point API.");
//        msg.setTo("+2349098123438");
//        new SmsResource().sendSingleText(auth, msg);
        //new SmsResource().querySentMessages();
        //System.out.println("encoded string::" + Util.getEncodeCredentials());
        EmailSender sender = new EmailSender();
        List<String> emails = new ArrayList<>();
        emails.add("emmanuel.idoko@africaprudential.com");
        EmailPlaceholder placeholder = new EmailPlaceholder();
        placeholder.setFirst_name("Idoko");
        placeholder.setLast_name("Emmanuel");
        placeholder.setCoop_name("Africa Prudential");
        placeholder.setUsername("emmanuel.idoko@africaprudential.com");
        placeholder.setPassword("desterity");
        
        //sender.sendSingleEmailOfiice365(placeholder.getUsername(), "Email Test", "create-member", placeholder);

        //emails.add("gbolahan.folarin@africaprudential.com");
        //javaEmail.createEmailMessage();
//        String subject = "Cooperative Licence Activation.";
//                String emailBody = "<p>The licence for the United Capital PLC"
//                        + ", has been activated successfully.</p>";
//        String template = Util.getEmailTemplate("C:\\Users\\emmanuel.idoko\\Documents\\development tools", "template.html");
//        sender.sendBulkEmail("killmewithdlord", "desterity", emails, subject,emailBody, template);
        
//    List<Product> products = Arrays.asList(
//    new Product( 1,"note1", 500), 
//    new Product( 2,"note2", 1000 ) );
//    Map<String, Object> root = new HashMap<String, Object>();
//    root.put( "products", products );
//    StringWriter out = new StringWriter();
//
//    Configuration cfg = new Configuration();
//    cfg.setClassForTemplateLoading( APITest.class, "/templates" );
//   // cfg.setObjectWrapper( new DefaultObjectWrapper() );
//    Template temp = cfg.getTemplate( "product.ftl" );
//    temp.process( root, out );
//
//    new APITest().sendGeneralEmail("emmytoonaiz@gmail.com", "testing table", "This is awesome!!!!", out);
//    System.out.println( out.getBuffer().toString() );
    
    
    }
    
    public void sendGeneralEmail(String to_email, String subject, String body, StringWriter writer) throws IOException, TemplateException {
        final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME_O365, SMTP_PASSWORD_O365);
            }

        });

        try {

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

