package com.dldhk97.kumohcafeteriaviewer.utility;

public class TimeUtility {
    private static TimeUtility _instance;

    private TimeUtility() {}

    public static TimeUtility getInstance(){
        if(_instance == null)
            _instance = new TimeUtility();
        return _instance;
    }

    public String hourMinToAMPM(int hour, int min){
        String ampm = "오전";
        if(hour > 12){
            ampm = "오후";
            hour -= 12;
        }
        if(hour == 0)
            hour = 12;
        String result = ampm + " " + hour + "시" + min + "분";
        return result;
    }


}
