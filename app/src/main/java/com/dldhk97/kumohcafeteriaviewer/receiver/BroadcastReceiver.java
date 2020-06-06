package com.dldhk97.kumohcafeteriaviewer.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.dldhk97.kumohcafeteriaviewer.MainActivity;
import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.data.DatabaseManager;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.Calendar;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    public static String ACTION_USER_NOTIFICATION = "com.dldhk97.kumohacfeteriaviewer.receiver.BroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_USER_NOTIFICATION)){
            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // 알림 ID 가져오기
            String notificationItemID = intent.getStringExtra("notificationItemID");
            if(notificationItemID == null){
                Log.d("aaaaa", "notificationItemID is null");
                return;
            }

            DatabaseManager.getInstance().setContextIfNotExist(context);        // DB매니저가 인스턴스화되지 않으면 초기화. 이거안하면 어플 실행안됬을때 터짐.

            // 알림 객체 가져오기
            NotificationItem notificationItem = NotificationItemManager.getInstance().findItem(notificationItemID);
            if(notificationItem == null){
                Log.d("aaaaa", "notificationItem is null");
                return;
            }

            // 설정 정보 가져오기
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            // 알림 객체가 유효한지(deactivated 된지, 너무 늦게 알림발생했는지) 체크
            if(!isNotificationItemValid(notificationItem, prefs)){
                Log.d("aaaaa", "notificationItem is invalid");
                return;
            }

            // 푸시 타이틀 및 내용 설정
            String pushTitle = notificationItem.getCafeteriaType().toString() + " - " + notificationItem.getMealTimeType() + " [" + notificationItem.getHour() + ":" + notificationItem.getMin() + "]";

            // 푸시컨텐츠가 없으면 알릴 필요 없다.
            Menu menu = getMenuFromMenuManager(notificationItem);
            if(menu == null) {
                Log.d("aaaaa", "Menu is null");
                return;
            }

            // 찜목록 체크. "찜목록만 알림 설정"이 꺼져있는 경우 그냥 true 반환함.
            if(!isFavoriteIncluded(menu, prefs)){
                Log.d("aaaaa", "notificationItem is invalid");
                return;
            }

            // 알림 빌더 생성
            boolean ring = prefs.getBoolean("ring", true);
            boolean vibrate = prefs.getBoolean("vibrate", true);
            String menuText = menu.toString();
            String menuSummary = menuText.replace("\n",", ");;
            NotificationCompat.Builder builder = buildNotification(context, pushTitle, menuSummary, menuText, ring, vibrate);

            // 알림
            notificationmanager.notify(1, builder.build());
        }

    }

    private static int validTimeMil = 600000;       // 10분
    private boolean isNotificationItemValid(NotificationItem notificationItem, SharedPreferences prefs){
        // 알림 아이템이 deactivated 인 경우
        if(!notificationItem.isActivated()){
            Log.d("aaaaa", "notificationItem is deactivated");
            return false;
        }

        // 알림이 10분 지나서 동작하면 유효하지 않은 알림으로 처리.
        Calendar targetTime = Calendar.getInstance();
        targetTime.set(Calendar.HOUR_OF_DAY, notificationItem.getHour());
        targetTime.set(Calendar.MINUTE, notificationItem.getMin());
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);

        Calendar now = Calendar.getInstance();
        
        long diffMil = Calendar.getInstance().getTimeInMillis() - targetTime.getTimeInMillis();
        if(diffMil >= validTimeMil){
            Log.d("aaaaa", "notificationItem time over : " + diffMil);
            return false;
        }

        // 주말이면 알리지 않음 체크되있으면, 주말인지 검사함.
        boolean no_notify_holiday = prefs.getBoolean("no_notify_holiday", false);
        if(no_notify_holiday){
            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek == 1 || dayOfWeek == 7){
                Log.d("aaaaa", "today is not holiday. dayofWeek : " + dayOfWeek);
                return false;
            }
        }

        return true;
    }

    // 메뉴 매니저에서 메뉴 객체 가져오기
    private Menu getMenuFromMenuManager(NotificationItem notificationItem){
        Menu menu = null;
        try {
            Calendar today = DateUtility.remainOnlyDate(Calendar.getInstance());
            WeekMenus weekMenus = MenuManager.getInstance().getWeekMenus(notificationItem.getCafeteriaType(), today, false);
            for(Menu m : weekMenus.get(today).getMenus()){
                if(m.getCafeteriaType() == notificationItem.getCafeteriaType()){
                    menu = m;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }

    // 찜목록 체크
    private boolean isFavoriteIncluded(Menu menu, SharedPreferences prefs){
        // 찜목록만 알리는 경우 체크
        boolean favorite_only = prefs.getBoolean("favorite_only", false);
        if(favorite_only){
            try{
                boolean isFavoriteContains = false;
                for(Item i : FavoriteManager.getInstance().getCurrentItems()){
                    if(menu.toString().contains(i.getItemName())){
                        isFavoriteContains = true;
                        break;
                    }
                }
                if(!isFavoriteContains){
                    Log.d("aaaaa", "Favorite is not contained.");
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    // 알림 생성
    private NotificationCompat.Builder buildNotification(Context context, String contentTitle, String contentTextSummary, String contentText, boolean ring, boolean vibrate){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_menu_main).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(contentTitle).setContentText(contentTextSummary)
                .setContentIntent(pendingIntent).setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText));

        if(ring && vibrate){
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
            Log.d("aaaaa", "Notification ALL");
        }
        else if(ring){
            builder.setDefaults(Notification.DEFAULT_SOUND);
            Log.d("aaaaa", "Notification SOUND ONLY");
        }
        else if(vibrate){
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            Log.d("aaaaa", "Notification VIBRATION ONLY");
        }
        return builder;
    }
}
