package com.dldhk97.kumohcafeteriaviewer.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

// 어떤 날의 조식/중식/석식 중 하나의 식단. 여러 음식을 포함함.
public class Menu implements Serializable {
    private Calendar date;                      // 날짜
    private CafeteriaType cafeteriaType;    // 식당
    private MealTimeType mealTimeType;      // 조식/중식/석식
    private ArrayList<Item> items;        // 음식 리스트
    private int imageId;

    public Menu(Calendar date, CafeteriaType cafeteriaType, MealTimeType mealTimeType){
        this.date = date;
        this.cafeteriaType = cafeteriaType;
        this.mealTimeType = mealTimeType;
        items = new ArrayList<>();
    }

    public Calendar getDate(){
        return this.date;
    }

    public CafeteriaType getCafeteriaType(){
        return this.cafeteriaType;
    }

    public MealTimeType getMealTimeType(){
        return this.mealTimeType;
    }

    public void addItem(Item item) throws Exception{
        items.add(item);
    }

    public Item getItem(int index) throws Exception{
        return items.get(index);
    }

    public ArrayList<Item> getItems(){
        return items;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getCount(){
        return items.size();
    }

    @NonNull
    @Override
    public String toString() {
        try{
            StringBuilder sb = new StringBuilder();
            for(Item i : items){
                sb.append(i.toString() + " ");
            }

            //ETC 추가할것
            return sb.toString();
        }
        catch(Exception e){
            Log.d("[Menu.toString]", e.getMessage());
        }
        return super.toString();
    }
}