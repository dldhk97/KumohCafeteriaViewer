package com.dldhk97.kumohcafeteriaviewer.enums;

import androidx.annotation.NonNull;

public enum MealTimeType {
    BREAKFAST, LUNCH, DINNER, ONECOURSE, UNKNOWN;

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case BREAKFAST:
                return getStringArray()[0];
            case LUNCH:
                return getStringArray()[1];
            case DINNER:
                return getStringArray()[2];
            case ONECOURSE:
                return getStringArray()[3];
            case UNKNOWN:
                return "알수없음";
            default:
                return super.toString();
        }
    }

    public static MealTimeType stringTo(String s){
        if(s.equals(getStringArray()[0])){
            return BREAKFAST;
        }
        else if(s.equals(getStringArray()[1])){
            return LUNCH;
        }
        else if(s.equals(getStringArray()[2])){
            return DINNER;
        }
        else if(s.equals(getStringArray()[3])){
            return ONECOURSE;
        }
        else if(s.equals("알수없음")){
            return UNKNOWN;
        }
        return UNKNOWN;
    }

    public static MealTimeType getByIndex(int index){
        switch (index){
            case 0:
                return BREAKFAST;
            case 1:
                return LUNCH;
            case 2:
                return DINNER;
            case 3:
                return ONECOURSE;
            default:
                return UNKNOWN;
        }
    }

    public String getEatableTime(){
        switch (this){
            case BREAKFAST:
                return "08:30~09:30";
            case LUNCH:
                return "11:30~14:00";
            case DINNER:
                return "17:30~18:30";
            default:
                return "";
        }
    }

    public static String[] getStringArray(){
        return new String[]{"조식", "중식", "석식", "일품요리"};
    }
}
