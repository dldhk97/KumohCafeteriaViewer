package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class OnCafeteriaSelectedListener implements AdapterView.OnItemSelectedListener {
    private final NotificationItem notificationItem;
    private final CafeteriaType orgCafeteriaType;

    public OnCafeteriaSelectedListener(final NotificationItem notificationItem){
        this.notificationItem = notificationItem;
        orgCafeteriaType =  notificationItem.getCafeteriaType();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try{
            CafeteriaType cafeteriaType = CafeteriaType.getByIndex(i);
            if(orgCafeteriaType != cafeteriaType){
                notificationItem.setCafeteriaType(cafeteriaType);
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
