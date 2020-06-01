package com.dldhk97.kumohcafeteriaviewer.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.dldhk97.kumohcafeteriaviewer.MainActivity;
import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.data.DatabaseManager;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.Calendar;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    public static String ACTION_ALARM = "com.dldhk97.kumohacfeteriaviewer.receiver.BroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 알림정보 가져오기
        String notificationItemID = intent.getStringExtra("notificationItemID");
        if(notificationItemID == null){
            Log.d("aaaaa", "notificationItemID is null");
            return;
        }

        DatabaseManager.getInstance().setContextIfNotExist(context);        // DB매니저가 인스턴스화되지 않으면 초기화.

        // 알림 객체 가져오기
        NotificationItem notificationItem = NotificationItemManager.getInstance().findItem(notificationItemID);
        if(notificationItem == null){
            Log.d("aaaaa", "notificationItem is null");
            return;
        }
        if(!notificationItem.isActivated()){
            Log.d("aaaaa", "notificationItem is deactivated");
            return;
        }

        // 알림이 10분 지나서 동작하면 그냥 무시함.
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, notificationItem.getHour());
        targetTime.set(Calendar.MINUTE, notificationItem.getMin());
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);
        long diffMil = Calendar.getInstance().getTimeInMillis() - targetTime.getTimeInMillis();
        if(diffMil >= 600000){
            Log.d("aaaaa", "notificationItem is not exact time : " + diffMil);
            return;
        }

        String pushTitle = notificationItem.getCafeteriaType().toString() + " - " + notificationItem.getMealTimeType() + " [" + notificationItem.getHour() + ":" + notificationItem.getMin() + "]";
        String pushContent = null;
        try {
            Calendar today = DateUtility.remainOnlyDate(Calendar.getInstance());
            WeekMenus weekMenus = MenuManager.getInstance().getWeekMenus(notificationItem.getCafeteriaType(), today, false);
            for(Menu m : weekMenus.get(today).getMenus()){
                if(m.getCafeteriaType() == notificationItem.getCafeteriaType()){
                    pushContent = m.toString();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pushContent == null){
            Log.d("aaaaa", "pushContent is null");
            return;
        }

        // 알림 생성(DEFAULT_VIBRATE는 진동설정 커스텀가능할 때 고치면됨)

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_menu_main).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(pushTitle).setContentText(pushContent.replace("\n",", "))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(pushContent));

        notificationmanager.notify(1, builder.build());
    }
}
