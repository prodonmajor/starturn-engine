/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.alert;

import com.starturn.engine.util.notification.model.Authentication;
import com.starturn.engine.util.notification.model.EmailMessage;
import com.starturn.engine.util.notification.model.EmailPlaceholder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Administrator
 */
public class AlertUtil {
   public static String getEncodeCredentials(Authentication auth) {
        if ((auth.getUsername() != null && !auth.getUsername().trim().isEmpty()) && (auth.getPassword() != null && !auth.getPassword().trim().isEmpty())) {
            String authString = auth.getUsername() + ":" + auth.getPassword();
            return (new Base64().encodeToString(authString.getBytes()));
        } else {
            return "Invalid parameters detected.";
        }
    }

    public static String getEmailTemplate(String tempatePath, String templateName) {
        Path wiki_path = Paths.get(tempatePath, templateName);

        Charset charset = Charset.forName("ISO-8859-1");
        StringBuilder sb = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(wiki_path, charset);

            int index = 0;
            System.out.println("Template path \t\t Index");
            for (String line : lines) {
                index++;
                System.out.println(line + "\t\t" + index);
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return sb.toString();
    }

    public Map<String, Object> getEmailTemplateModel(String templateName, EmailMessage message) {

        Map<String, Object> model = new HashMap<>();
//        template_list.add("create-member");
//        template_list.add("increase-shares-limit");
//        template_list.add("loan-approval-request");
//        template_list.add("post-financial-entries");
        if (templateName == null || templateName.trim().isEmpty()) {
            templateName = "general";
        }
        
        EmailPlaceholder placeholder = message.getPlaceholder();

        if (templateName.equalsIgnoreCase("create-member")) {
            model.put("firstName", placeholder.getFirst_name());
            //model.put("lastName", placeholder.getLast_name());
            model.put("location", placeholder.getCoop_name());
            model.put("comp_name", placeholder.getCoop_name());
            model.put("member_username", placeholder.getUsername());
            model.put("password", placeholder.getPassword());
            model.put("token", placeholder.getToken());
        }

        if (templateName.equalsIgnoreCase("increase-shares-limit")) {
            model.put("firstName", placeholder.getFirst_name());
            model.put("lastName", placeholder.getLast_name());
            model.put("trans_type", placeholder.getTnx());
            model.put("transaction_units", placeholder.getTransaction_units());
            model.put("balance", placeholder.getBalance());
            model.put("coop_name", placeholder.getCoop_name());
        }

        if (templateName.equalsIgnoreCase("loan-approval-request")) {
            model.put("firstName", placeholder.getFirst_name());
            model.put("lastName", placeholder.getLast_name());
            model.put("loan_status", placeholder.getLoan_status());
            model.put("coop_admin_com", placeholder.getCoop_admin_comment());
            model.put("coop_name", placeholder.getCoop_name());
        }

        if (templateName.equalsIgnoreCase("post-financial-entries")) {
            model.put("firstName", placeholder.getFirst_name());
            model.put("lastName", placeholder.getLast_name());
            model.put("trans_type", placeholder.getTnx());
            model.put("username", placeholder.getUsername());
            model.put("amount", placeholder.getAmount());
            model.put("trans_id", placeholder.getDesc());
            model.put("balance", placeholder.getBalance());
            model.put("trans_date", placeholder.getTransaction_date());
            model.put("coop_admin_com", placeholder.getCoop_admin_comment());
            model.put("coop_name", placeholder.getCoop_name());
        }

        if (templateName.equalsIgnoreCase("general")) {
            model.put("firstName", placeholder.getFirst_name());
            model.put("lastName", placeholder.getLast_name());
            model.put("coop_name", placeholder.getCoop_name());
            model.put("body", message.getEmailBody());
        }

        return model;
    } 
}
