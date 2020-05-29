package com.dldhk97.kumohcafeteriaviewer.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtility {
    // 문자열을 Calender로 변환
    public static Calendar stringToDate(String str) throws Exception{
        DateFormat formmater = new SimpleDateFormat("yyyy.MM.dd");
        Date date = formmater.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String dateToString(Calendar date, char divider){
        SimpleDateFormat format = new SimpleDateFormat("yyyy" + divider + "MM" + divider +"dd");
        return format.format(date.getTime());
    }

    public static Calendar remainOnlyDate(Calendar date) throws Exception{
        return stringToDate(dateToString(date, '.'));
    }
}
