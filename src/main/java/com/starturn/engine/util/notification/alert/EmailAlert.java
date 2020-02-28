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

/**
 *
 * @author Administrator
 */
public class EmailAlert {

    private final Client client;
    private WebTarget baseTarget;
    private final String url = "https://107.20.199.106/email/1/send";
//    public static final String API_USER = "484_appdev@africaprudential.com";
//    public static final String API_PASSPHRASE = "a774b3a81c25ca5d80d0ecdchangepassphrase";

    public EmailAlert() {
        this.client = ClientBuilder.newClient();
        this.baseTarget = this.client.target(url);
    }
    
    public void sendSingleEmail(MessageDetails msg) {
        //implement
    }

    public void sendSingleEmail(Authentication auth, MessageDetails msg) {
        try {
            Gson gson = new Gson();
            String payload = gson.toJson(msg);
            System.out.println("payload before sending..." + payload);
            baseTarget = client.target(url);
            String response = baseTarget.request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic amVmZm9ub2NoOkF1ZHJleUAzMjE=")
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .post(Entity.json(payload))
                    .readEntity(String.class);
            System.out.println("Response from email:::" + response);
        } catch (Exception ex) {
            // ex.printStackTrace();
            System.out.println("Error occured due to: " + ex);
        }
    }

}
