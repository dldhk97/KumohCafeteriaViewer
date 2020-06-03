package com.dldhk97.kumohcafeteriaviewer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.dldhk97.kumohcafeteriaviewer.data.DatabaseManager;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.DayMenus;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link KCVWidgetConfigureActivity KCVWidgetConfigureActivity}
 */
public class KCVWidget extends AppWidgetProvider {

    private static final String PREFS_NAME = "com.dldhk97.kumohcafeteriaviewer.KCVWidget";
    private static final String PREF_PREFIX_KEY = "KCVWidget_";

    private static final String ACTION_WIDGET_MEALTIME_CHANGE = "ACTION_WIDGET_MEALTIME_CHANGE";
    private static final String ACTION_WIDGET_FORCE_UPDATE = "ACTION_WIDGET_FORCE_UPDATE";

    private static final int ITEM_NAME_MAX_LENGTH = 10;     // 아이템이름이 n자리 이상이면 줄바꿈.


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // ConfigureActivity에서 저장한 정보들 빼온다.
        ArrayList<String> loadedPrefs = KCVWidgetConfigureActivity.loadPrefs(context, appWidgetId);
        CafeteriaType cafeteriaType = CafeteriaType.stringTo(loadedPrefs.get(0));
        boolean isBlack = Boolean.parseBoolean(loadedPrefs.get(1));
        int transparent = Integer.parseInt(loadedPrefs.get(2));
        MealTimeType mealTimeType = MealTimeType.stringTo(loadedPrefs.get(3));

        RemoteViews views;

        // Sharedpreference에서 정보 가져온다.
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String isBigStr = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "isBig", "false");
        boolean isBig = Boolean.parseBoolean(isBigStr);
        boolean isAutomaticChangeMealtime = prefs.getBoolean("widget_automatic_change_mealtime", true);
        long lastMealTimeChanged = prefs.getLong(PREF_PREFIX_KEY + appWidgetId + "lastMealTimeChanged", -1);

        // 시간에 맞춰 mealTime 변경하기가 true 인 경우
        if(isAutomaticChangeMealtime){
            Calendar now = Calendar.getInstance();
            // 수동으로 위젯 변경한지 10분 이상 지났으면, AutoChange 한다.
            if(now.getTimeInMillis() - lastMealTimeChanged > 600000){
                MealTimeType type = autoChangeMealTimeType(context, now);
                if(type != null)
                    mealTimeType = type;
            }
        }

        // 현재 오늘 메뉴목록 가져오기
        ArrayList<Menu> currentMenus = null;
        try{
            Calendar today = DateUtility.remainOnlyDate(Calendar.getInstance());
            DatabaseManager.getInstance().setContextIfNotExist(context);
            WeekMenus weekMenus = MenuManager.getInstance().getWeekMenus(cafeteriaType, today, false);
            if(weekMenus != null){
                DayMenus dayMenus = weekMenus.get(today);
                if(dayMenus != null)
                    currentMenus = dayMenus.getMenus();
            }
            else{
                Log.d("aaaaa", "weekMenus = null");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // 위젯 식사시간 변경 이벤트
        Intent mealTimeChangeIntent = new Intent(context, KCVWidget.class);
        mealTimeChangeIntent.setAction(ACTION_WIDGET_MEALTIME_CHANGE);
        mealTimeChangeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent mealTimeChangePending = PendingIntent.getBroadcast(context, appWidgetId, mealTimeChangeIntent, 0);

        // 위젯 리프레시 이벤트
        Intent refreshIntent = new Intent(context, KCVWidget.class);
        refreshIntent.setAction(ACTION_WIDGET_FORCE_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, 0);

        // 어플 실행 이벤트
        Intent appStartIntent = new Intent(Intent.ACTION_MAIN);
        appStartIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appStartIntent.setComponent(new ComponentName(context, MainActivity.class));
        PendingIntent appStartPending = PendingIntent.getActivity(context, 0, appStartIntent, 0);

        // 크기 알아내서 알맞는 레이아웃 적용
        if(isBig){
            views = new RemoteViews(context.getPackageName(), R.layout.k_c_v_widget_big);

            // 더러운 식단 설정
            int cnt = 0;
            if(currentMenus != null){
                for(Menu m : currentMenus){
                    if(cnt < 3){
                        String title = m.getMealTimeType().toString();
                        String menuStr = "";
                        for(Item i : m.getItems()){
                            String itemName = i.getItemName();
                            if(i.getItemType() == ItemType.DIVIDER){
                                itemName = "------------";
                            }
                            else{
                                if(itemName.length() > ITEM_NAME_MAX_LENGTH)
                                    itemName = itemName.substring(0, ITEM_NAME_MAX_LENGTH) + "\n" + itemName.substring(ITEM_NAME_MAX_LENGTH);
                            }
                            menuStr += itemName + "\n";
                        }

                        if(cnt == 0){
                            views.setTextViewText(R.id.widget_big_textView_title_menu1, title);
                            views.setTextViewText(R.id.widget_big_textView_menus_menu1, menuStr);
                        }
                        else if(cnt == 1){
                            views.setTextViewText(R.id.widget_big_textView_title_menu2, title);
                            views.setTextViewText(R.id.widget_big_textView_menus_menu2, menuStr);
                        }
                        else if(cnt == 2){
                            views.setTextViewText(R.id.widget_big_textView_title_menu3, title);
                            views.setTextViewText(R.id.widget_big_textView_menus_menu3, menuStr);
                        }
                    }
                    cnt++;
                }
            }

            views.setTextViewText(R.id.widget_big_textView_cafeteria, cafeteriaType.toString());           // 식당 설정
            views.setOnClickPendingIntent(R.id.widget_big_textView_cafeteria, refreshPending);             // 새로고침 이벤트 등록
            views.setOnClickPendingIntent(R.id.widget_big_textView_menus_layout, appStartPending);        // 앱 실행 이벤트 등록
            setBackgroundColor(true,isBlack,views,transparent);
            setForegroundColors(true, isBlack, views, transparent);
        }
        else{
            // 노멀 뷰
            views = new RemoteViews(context.getPackageName(), R.layout.k_c_v_widget);

            // 위젯에 설정된 시간의 메뉴만 가져온다.
            String menuStr = "정보 없음";
            if(currentMenus != null){
                for(Menu m : currentMenus){
                    if(m.getMealTimeType() == mealTimeType){
                        menuStr = m.toString();
                        break;
                    }
                }
            }
            views.setTextViewText(R.id.widget_textView_menus, menuStr);                               // 식단 설정
            views.setTextViewText(R.id.widget_textView_cafeteria, cafeteriaType.toString());          // 식당 텍스트 설정
            views.setOnClickPendingIntent(R.id.widget_textView_mealTime, mealTimeChangePending);      // 식사시간 변경 클릭 시 이벤트 등록
            views.setTextViewText(R.id.widget_textView_mealTime, mealTimeType.toString());            // 식사시간 텍스트 설정
            views.setOnClickPendingIntent(R.id.widget_textView_cafeteria, refreshPending);            // 새로고침 이벤트 등록
            views.setOnClickPendingIntent(R.id.widget_textView_menus, appStartPending);               // 앱 실행 이벤트 등록
            setBackgroundColor(false,isBlack,views,transparent);
            setForegroundColors(false, isBlack, views, transparent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // ---------------------------------------------------------------------------------

    private static MealTimeType autoChangeMealTimeType(Context context, Calendar now){
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int nowMin = now.get(Calendar.MINUTE);
        String[] eatableTimes = context.getResources().getStringArray(R.array.eatableTime);

        int hour = -1;
        int min = -1;
        MealTimeType type = null;
        for(int index = 0; index < eatableTimes.length - 1 ; index++){
            String[] splited =  eatableTimes[index].split("~")[1].split(":");
            hour = Integer.parseInt(splited[0]);
            min = Integer.parseInt(splited[1]);

            if(nowHour < hour){
                type = MealTimeType.getByIndex(index);
                break;
            }
            else if(nowHour == hour){
                if(nowMin <= min){
                    type = MealTimeType.getByIndex(index);
                    break;
                }
            }
        }

        if(type != null)
            Log.d("aaaaa", "autoChangeMealTimeType : " + type.toString());

        return type;
    }

    // --------------------------------------------------------------

    private static void setForegroundColors(boolean isBig, boolean isBlack, RemoteViews views, int transparent){
        String foregroundColor = null;
        if(transparent >= 50){
            if(isBlack){
                foregroundColor = "#FFFFFFFF";
            }
            else{
                foregroundColor = "#FF000000";
            }
        }
        else{
            if(isBlack){
                foregroundColor = "#FF000000";
            }
            else{
                foregroundColor = "#FFFFFFFF";
            }
        }

        if(isBig){
            views.setInt(R.id.widget_big_textView_cafeteria, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_title_menu1, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_title_menu2, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_title_menu3, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_menus_menu1, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_menus_menu2, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_big_textView_menus_menu3, "setTextColor", Color.parseColor(foregroundColor));
        }
        else{
            views.setInt(R.id.widget_textView_cafeteria, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_textView_mealTime, "setTextColor", Color.parseColor(foregroundColor));
            views.setInt(R.id.widget_textView_menus, "setTextColor", Color.parseColor(foregroundColor));
        }
    }

    private static void setBackgroundColor(boolean isBig, boolean isBlack, RemoteViews views, int transparent){
        String transparentStr = String.valueOf(transparent);
        // 투명도를 int에서 FF or 두자리수로 수정
        if(transparentStr.equals("100")){
            transparentStr = "FF";
        }
        else if(transparentStr.length() < 2){
            transparentStr = "0" + transparentStr;
        }

        // 배경색 & 투명도 설정
        String backgroundColor = null;
        if(isBlack){
            backgroundColor = "000000";
        }
        else{
            backgroundColor = "FFFFFF";
        }
        backgroundColor = "#" + transparentStr + backgroundColor;

        if(isBig){
            views.setInt(R.id.widget_big_background, "setBackgroundColor", Color.parseColor(backgroundColor));
        }
        else{
            views.setInt(R.id.widget_background, "setBackgroundColor", Color.parseColor(backgroundColor));
        }
    }

    // ---------------------------------------------------------------------------------

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            KCVWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

//        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
//        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
//        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        RemoteViews rv = null;

        // 크기에 따라 레이아웃 변경
        boolean isBig = maxWidth >= 428 ? true : false;
        if(isBig){
            rv = new RemoteViews(context.getPackageName(), R.layout.k_c_v_widget_big);
        } else {
            rv = new RemoteViews(context.getPackageName(), R.layout.k_c_v_widget);
        }

        // 레이아웃이 큰놈인지 아닌지 상태 저장
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "isBig", String.valueOf(isBig));        // 레이아웃 유형 저장(큰놈인지 아닌지)
        prefs.apply();

        updateAppWidget(context, appWidgetManager, appWidgetId);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action != null) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            switch (action){
                case ACTION_WIDGET_MEALTIME_CHANGE:    // 식사시간 클릭되었을 때 식사시간 변경한다!
                    changeMealTime(context, appWidgetId);
                    break;
                case ACTION_WIDGET_FORCE_UPDATE:
                    forceUpdate(context, appWidgetId);
                    break;
            }

            // 새로고침은, 파싱 강제로 함.
            updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId); // 위젯 새로고침 수행
        }
        super.onReceive(context, intent);
    }

    // ----------------------------------------------

    private void changeMealTime(Context context, int appWidgetId){
        // 현재 mealTimeType 가져오기
        ArrayList<String> loadedPrefs = KCVWidgetConfigureActivity.loadPrefs(context, appWidgetId);
        String mealTimeTypeStr = loadedPrefs.get(3);

        // 조식->중식->석식->일품요리->조식 순으로 변경
        MealTimeType mealTimeType = MealTimeType.stringTo(mealTimeTypeStr);
        switch (mealTimeType){
            case BREAKFAST:
                mealTimeType = MealTimeType.LUNCH;
                break;
            case LUNCH:
                mealTimeType = MealTimeType.DINNER;
                break;
            case DINNER:
                mealTimeType = MealTimeType.ONECOURSE;
                break;
            case ONECOURSE:
                mealTimeType = MealTimeType.BREAKFAST;
                break;
            default:
                mealTimeType = MealTimeType.BREAKFAST;
                break;
        }

        // 지금 시간 구하기
        long now = Calendar.getInstance().getTimeInMillis();

        // mealTimeType 변경
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "mealTimeType", mealTimeType.toString());           // 식사시간 설정
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId + "lastMealTimeChanged", now);                          // 마지막으로 수동 변경한 시간 기록 (시간에 맞게 식사시간 변경하는 기능을 위함. 최근에 변경되었으면 업데이트 하지 않게.)
        prefs.apply();
    }

    private void forceUpdate(Context context, int appWidgetId){
        try{
            // 오늘 알아내기
            Calendar today = Calendar.getInstance();
            DateUtility.remainOnlyDate(today);

            // 위젯에 설정된 식당 알아내기
            ArrayList<String> loadedPrefs = KCVWidgetConfigureActivity.loadPrefs(context, appWidgetId);
            CafeteriaType cafeteriaType = CafeteriaType.stringTo(loadedPrefs.get(0));

            // 파싱 후 업데이트
            MenuManager.getInstance().parseAndUpdate(cafeteriaType, today);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

