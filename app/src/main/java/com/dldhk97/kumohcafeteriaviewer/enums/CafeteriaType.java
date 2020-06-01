package com.dldhk97.kumohcafeteriaviewer.enums;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum CafeteriaType implements Serializable {
    STUDENT, STAFF, SNACKBAR, PUROOM, OREUM1, OREUM3, UNKNOWN;

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case STUDENT:
                return getStringArray()[0];
            case STAFF:
                return getStringArray()[1];
            case SNACKBAR:
                return getStringArray()[2];
            case PUROOM:
                return getStringArray()[3];
            case OREUM1:
                return getStringArray()[4];
            case OREUM3:
                return getStringArray()[5];
            case UNKNOWN:
                return "알수없음";
            default:
                return super.toString();
        }
    }

    public String getURL(){
        switch(this){
            case STUDENT:
                return "https://kumoh.ac.kr/ko/restaurant01.do?";
            case STAFF:
                return "https://kumoh.ac.kr/ko/restaurant02.do?";
            case SNACKBAR:
                return "https://kumoh.ac.kr/ko/restaurant04.do?";
            case PUROOM:
                return "http://dorm.kumoh.ac.kr/dorm/restaurant_menu01.do?";
            case OREUM1:
                return "http://dorm.kumoh.ac.kr/dorm/restaurant_menu02.do?";
            case OREUM3:
                return "http://dorm.kumoh.ac.kr/dorm/restaurant_menu03.do?";
            case UNKNOWN:
                return "알수없음";
            default:
                return "";
        }
    }

    public static CafeteriaType stringTo(String s){
        if(s.equals(getStringArray()[0])){
            return STUDENT;
        }
        else if(s.equals(getStringArray()[1])){
            return STAFF;
        }
        else if(s.equals(getStringArray()[2])){
            return SNACKBAR;
        }
        else if(s.equals(getStringArray()[3])){
            return PUROOM;
        }
        else if(s.equals(getStringArray()[4])){
            return OREUM1;
        }
        else if(s.equals(getStringArray()[5])){
            return OREUM3;
        }
        return UNKNOWN;
    }

    public static CafeteriaType getByIndex(int index){
        switch (index){
            case 0:
                return STUDENT;
            case 1:
                return STAFF;
            case 2:
                return SNACKBAR;
            case 3:
                return PUROOM;
            case 4:
                return OREUM1;
            case 5:
                return OREUM3;
            default:
                return UNKNOWN;
        }
    }

    public static String[] getStringArray(){
        return new String[]{"학생식당", "교직원식당", "분식당", "푸름관", "오름관1동", "오름관3동"};
    }
}
