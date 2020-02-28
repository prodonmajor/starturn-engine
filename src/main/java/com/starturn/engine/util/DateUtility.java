/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class DateUtility {
    private static final Logger logger = LogManager.getLogger(DateUtility.class);

    public long returnDateDiffInDays(Date date1, Date date2) {
        LocalDate ld1 = new Date(date1.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ld2 = new Date(date2.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long days = ChronoUnit.DAYS.between(ld1, ld2);
        return days;
    }

    public long returnDateDiffInMonths(Date date1, Date date2) {
        LocalDate ld1 = new Date(date1.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ld2 = new Date(date2.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        long months = ChronoUnit.MONTHS.between(ld1, ld2);
        return months;
    }

    public int getJavaDateCalendarFieldId(String fieldName) {
        if (fieldName.equalsIgnoreCase("day")) {
            return Calendar.DAY_OF_YEAR;
        }

        if (fieldName.equalsIgnoreCase("week")) {
            return Calendar.WEEK_OF_YEAR;
        }

        if (fieldName.equalsIgnoreCase("month")) {
            return Calendar.MONTH;
        }

        if (fieldName.equalsIgnoreCase("year")) {
            return Calendar.YEAR;
        }
        return 0;
    }

    public Date addToDate(Date dateToAddTo, int increaseBy, int period) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToAddTo);
        calendar.add(period, increaseBy);

        return calendar.getTime();
    }

    public String addToDate(String dateToAddTo, int increaseBy, int period) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatter.parse(dateToAddTo));
        calendar.add(period, increaseBy);

        return formatter.format(calendar.getTime());
    }
}
