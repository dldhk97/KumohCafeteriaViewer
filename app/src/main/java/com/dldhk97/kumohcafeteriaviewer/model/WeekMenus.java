package com.dldhk97.kumohcafeteriaviewer.model;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class WeekMenus implements Serializable {
    private ArrayList<DayMenus> dayMenuList = new ArrayList<>();
    private final Calendar startDate;
    private final CafeteriaType cafeteriaType;

    public WeekMenus(Calendar startDate , CafeteriaType cafeteriaType){
        this.startDate = startDate;
        this.cafeteriaType = cafeteriaType;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }

    public void add(DayMenus dayMenus){
        this.dayMenuList.add(dayMenus);
    }

    public DayMenus get(Calendar date){
        for(DayMenus dm : dayMenuList){
            if(dm.getDate().compareTo(date) == 0){
                return dm;
            }
        }
        return null;
    }

    public boolean contains(Calendar date){
        for(DayMenus dm : dayMenuList){
            if(dm.getDate().compareTo(date) == 0){
                return true;
            }
        }
        return false;
    }
}
