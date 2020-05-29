package com.dldhk97.kumohcafeteriaviewer.model;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

// 하루의 식단을 모두 가진 클래스
public class DayMenus implements Serializable {
    private final Calendar date;
    private final CafeteriaType cafeteriaType;
    private ArrayList<Menu> menus;

    public DayMenus(final Calendar date, CafeteriaType cafeteriaType){
        this.date = date;
        this.cafeteriaType = cafeteriaType;
        menus = new ArrayList<>();
    }

    public Calendar getDate() {
        return date;
    }
    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }
    public ArrayList<Menu> getMenus() {
        return menus;
    }

}
