/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Administrator
 */
public class EmailUtil {
     private static final Logger LOGGER = LogManager.getLogger(EmailUtil.class);

    public static String getEncodeCredentials(String username, String password) {
        if ((username != null && !username.trim().isEmpty()) && (password != null && !password.trim().isEmpty())) {
            String authString = username + ":" + password;
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
            for (String line : lines) {
                sb.append(line).append("\n");
            }
        } catch (IOException ex) {
            LOGGER.error("error thrown when getting template: " + ex);
        }
        return sb.toString();
    }
}
