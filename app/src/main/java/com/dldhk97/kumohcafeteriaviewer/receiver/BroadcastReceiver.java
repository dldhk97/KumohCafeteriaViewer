package com.dldhk97.kumohcafeteriaviewer.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.MainActivity;
import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d("aaaaa", "알림 받음");

        // 알림정보 가져오기
        String notificationItemID = intent.getStringExtra("notificationItemID");
        NotificationItem notificationItem = NotificationItemManager.getInstance().findItem(notificationItemID);

        String pushTitle = notificationItem.getName();
        String pushContent = notificationItem.getCafeteriaType().toString();


        // 알림 생성(DEFAULT_VIBRATE는 진동설정 커스텀가능할 때 고치면됨)
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_menu_main).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(pushTitle).setContentText(pushContent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
        notificationmanager.notify(1, builder.build());
    }
}
