/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class PolicyValidator {

    public static boolean validatePassword(String password, String passwordPolicy) {
        Pattern pattern = Pattern.compile(passwordPolicy);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
