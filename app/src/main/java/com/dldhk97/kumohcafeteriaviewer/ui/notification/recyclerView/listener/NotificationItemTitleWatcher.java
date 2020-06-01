package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class NotificationItemTitleWatcher implements TextWatcher {

    private final NotificationItem notificationItem;

    public NotificationItemTitleWatcher(final NotificationItem notificationItem){
        this.notificationItem = notificationItem;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        try{
            String editedName = editable.toString();
            if(editedName != null && !editedName.isEmpty()){
                notificationItem.setName(editedName);
                NotificationItemManager.getInstance().updateItem(notificationItem);
            }

        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }
}
