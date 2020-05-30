package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener;

import android.view.View;
import android.widget.AdapterView;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class OnMealTimeSelectedListener implements AdapterView.OnItemSelectedListener {

    private final NotificationItem notificationItem;
    private final MealTimeType orgMealTimeType;

    public OnMealTimeSelectedListener(final NotificationItem notificationItem){
        this.notificationItem = notificationItem;
        this.orgMealTimeType = notificationItem.getMealTimeType();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try{
            MealTimeType mealTimeType = MealTimeType.getByIndex(i);
            if(orgMealTimeType != mealTimeType){
                notificationItem.setMealTimeType(mealTimeType);
                NotificationItemManager.getInstance().updateItem(notificationItem);
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
