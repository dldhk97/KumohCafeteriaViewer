package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener;

import android.widget.CompoundButton;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class OnSwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    private final NotificationItem notificationItem;

    public OnSwitchCheckListener(final NotificationItem notificationItem){
        this.notificationItem = notificationItem;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try{
            notificationItem.setActivated(b);
            NotificationItemManager.getInstance().updateItem(notificationItem);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

    }
}
