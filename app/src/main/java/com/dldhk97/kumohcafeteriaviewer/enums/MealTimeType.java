package com.dldhk97.kumohcafeteriaviewer.enums;

import androidx.annotation.NonNull;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.utility.ResourceUtility;

public enum MealTimeType {
    BREAKFAST, LUNCH, DINNER, ONECOURSE, UNKNOWN;

    public static MealTimeType strToValue(String str){
        switch(str){
            case "조식":
                return BREAKFAST;
            case "중식":
                return LUNCH;
            case "석식":
                return DINNER;
            case "일품요리":
                return ONECOURSE;
            default:
                return UNKNOWN;
        }
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case BREAKFAST:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType)[0];
            case LUNCH:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType)[1];
            case DINNER:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType)[2];
            case ONECOURSE:
                return ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType)[3];
            case UNKNOWN:
                return "알수없음";
            default:
                return super.toString();
        }
    }

    public static MealTimeType stringTo(String s){
        final String[] mealTimeArr = ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType);
        if(mealTimeArr[0].equals(s)){
            return BREAKFAST;
        }
        else if(mealTimeArr[1].equals(s)){
            return LUNCH;
        }
        else if(mealTimeArr[2].equals(s)){
            return DINNER;
        }
        else if(mealTimeArr[3].equals(s)){
            return ONECOURSE;
        }
        else if(mealTimeArr[4].equals(s)){
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
}
