package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener;

import android.content.Context;
import android.widget.CompoundButton;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class OnSwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    private final NotificationItem notificationItem;
    private final Context context;

    public OnSwitchCheckListener(final NotificationItem notificationItem, final Context context){
        this.notificationItem = notificationItem;
        this.context = context;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try{
            // DB에 저장
            notificationItem.setActivated(b);
            NotificationItemManager.getInstance().updateItem(notificationItem);

            // 알림 세팅
            NotificationItemManager.getInstance().updateNotification(notificationItem, context);

            if(b){
                UIHandler.getInstance().showToast(notificationItem.getName() + " 이(가) 설정되었습니다.");
            }
            else{
                UIHandler.getInstance().showToast(notificationItem.getName() + " 이(가) 해제되었습니다.");
            }

        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

    }
}
