package com.dldhk97.kumohcafeteriaviewer.model;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;

public class NotificationItem {
    private String id;
    private String name;
    private CafeteriaType cafeteriaType;
    private MealTimeType mealTimeType;
    private int hour;
    private int min;
    private boolean activated;

    public NotificationItem(String name, CafeteriaType cafeteriaType, MealTimeType mealTimeType, int hour, int min, boolean activated) {
        this.name = name;
        this.cafeteriaType = cafeteriaType;
        this.mealTimeType = mealTimeType;
        this.hour = hour;
        this.min = min;
        this.activated = activated;
    }

    public NotificationItem(String id, String name, CafeteriaType cafeteriaType, MealTimeType mealTimeType, int hour, int min, boolean activated) {
        this.id = id;
        this.name = name;
        this.cafeteriaType = cafeteriaType;
        this.mealTimeType = mealTimeType;
        this.hour = hour;
        this.min = min;
        this.activated = activated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }

    public void setCafeteriaType(CafeteriaType cafeteriaType) {
        this.cafeteriaType = cafeteriaType;
    }

    public MealTimeType getMealTimeType() {
        return mealTimeType;
    }

    public void setMealTimeType(MealTimeType mealTimeType) {
        this.mealTimeType = mealTimeType;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
