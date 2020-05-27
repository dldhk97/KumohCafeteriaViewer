package com.dldhk97.kumohcafeteriaviewer.enums;

import androidx.annotation.NonNull;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.utility.ResourceUtility;

public enum CafeteriaType {
    STUDENT, STAFF, SNACKBAR, PUROOM, OREUM1, OREUM3, UNKNOWN;

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case STUDENT:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[0];
            case STAFF:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[1];
            case SNACKBAR:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[2];
            case PUROOM:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[3];
            case OREUM1:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[4];
            case OREUM3:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[5];
            case UNKNOWN:
                return "알 수 없음";
            default:
                return super.toString();
        }
    }

    public String getURL(){
        switch(this){
            case STUDENT:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[0];
            case STAFF:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[1];
            case SNACKBAR:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[2];
            case PUROOM:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[3];
            case OREUM1:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[4];
            case OREUM3:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.urls)[5];
            case UNKNOWN:
                return "알 수 없음";
            default:
                return "";
        }
    }

//
//   public static String[] getStringArray(){
//        return MainActivity.getInstance().getResources().getStringArray(R.array.cafeteriaType);
//    }
}