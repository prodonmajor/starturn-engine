/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util.notification.alert;

import com.google.gson.Gson;
import com.starturn.engine.util.notification.model.Authentication;
import com.starturn.engine.util.notification.model.MessageDetails;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

/**
 *
 * @author Administrator
 */

public class TextAlert {
     private final Client client;
    private WebTarget smsUrlTarget;
    private final String sendSmsUrl = "http://107.20.199.106/restapi/sms/1/text/single";

    public TextAlert() {
        this.client = ClientBuilder.newClient();
        this.smsUrlTarget = this.client.target(sendSmsUrl);
    }
    
    public void sendSingleText(MessageDetails msg) {
        //to implement
        String username = "emmanuel.idoko";
        String password = "Password@1";
        try {
            Authentication auth = new Authentication();
            auth.setUsername(username);
            auth.setPassword(password);
            
            Gson gson = new Gson();
            String payload = gson.toJson(msg);
            System.out.println("payload before sending..." + payload);
            smsUrlTarget = client.target(sendSmsUrl);
            String response = smsUrlTarget.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + AlertUtil.getEncodeCredentials(auth))
                    .post(Entity.json(payload))
                    .readEntity(String.class);
            System.out.println("Response:::" + response);
        } catch (Exception ex) {
            System.out.println("Error occured due to: " + ex);
        }
        
    }
    
    public void sendSingleText(Authentication auth, MessageDetails msg) {
        try {
            Gson gson = new Gson();
            String payload = gson.toJson(msg);
            System.out.println("payload before sending..." + payload);
            smsUrlTarget = client.target(sendSmsUrl);
            String response = smsUrlTarget.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + AlertUtil.getEncodeCredentials(auth))
                    .post(Entity.json(payload))
                    .readEntity(String.class);
            System.out.println("Response:::" + response);
        } catch (Exception ex) {
            System.out.println("Error occured due to: " + ex);
        }
    }
}

