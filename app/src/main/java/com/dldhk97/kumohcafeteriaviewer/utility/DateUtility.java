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

    public static String getDayOfWeek(Calendar date){
        if(date == null)
            return "알 수 없음";
        switch(date.get(Calendar.DAY_OF_WEEK)){
            case Calendar.SUNDAY:
                return "일";
            case Calendar.MONDAY:
                return "월";
            case Calendar.TUESDAY:
                return "화";
            case Calendar.WEDNESDAY:
                return "수";
            case Calendar.THURSDAY:
                return "목";
            case Calendar.FRIDAY:
                return "금";
            case Calendar.SATURDAY:
                return "토";
            default:
                return "알 수 없음";
        }
    }

    public static Calendar milToCalendar(long mil){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mil);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return calendar;
    }
}
