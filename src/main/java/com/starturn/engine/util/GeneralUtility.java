/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class GeneralUtility {
  /**
     * Checks if email is valid.
     *
     * @param emailAddress the email address to check
     * @return true, if email is valid
     */
    public boolean emailIsValid(String emailAddress) {
        String email_pattern = "[a-zA-Z0-9]+([\\._a-zA-Z0-9]+)*@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,})*(\\.[a-zA-Z]{2,})";
        Pattern pattern = Pattern.compile(email_pattern);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public String returnCurrentTimeWithAMorPM() {
        Date date = new Date();
        String strDateFormat = "HH:mm:ss a";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(date);
    }

    /**
     * generates random six digits token
     *
     * @return the token generated
     */
    public String getRandomNumberToken() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    public long getDateDiffInMins(Date tokenCreationDate) {
        long total_mins = 0;
        try {

            //in milliseconds
            long diff = new Date().getTime() - tokenCreationDate.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            total_mins = (diffDays * 60 * 24) + (diffHours * 60) + (diffMinutes);

//            System.out.print(diffDays + " days, ");
//            System.out.print(diffHours + " hours, ");
//            System.out.print(diffMinutes + " minutes, ");
//            System.out.println(diffSeconds + " seconds.");
//            System.out.println(total_mins + " total minutes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total_mins;
    }

    public int compareTwoDates(Date currentDate, Date dateToCompare) {
        LocalDate ld1 = new Date(currentDate.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ld2 = new Date(dateToCompare.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return ld1.compareTo(ld2);
    }  
}
