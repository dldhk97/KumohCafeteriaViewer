package com.dldhk97.kumohcafeteriaviewer;

import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.parser.Parser;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.Calendar;
import java.util.TreeMap;

public class MenuManager {
    private static MenuManager _instance;
    private TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> menusTreeMap;      // <음식점타입<시작일,주메뉴들>>

    private MenuManager(){
        menusTreeMap = new TreeMap<>();
        menusTreeMap.put(CafeteriaType.STUDENT, new TreeMap<Calendar, WeekMenus>());
        menusTreeMap.put(CafeteriaType.STAFF, new TreeMap<Calendar, WeekMenus>());
        menusTreeMap.put(CafeteriaType.SNACKBAR, new TreeMap<Calendar, WeekMenus>());
        menusTreeMap.put(CafeteriaType.PUROOM, new TreeMap<Calendar, WeekMenus>());
        menusTreeMap.put(CafeteriaType.OREUM1, new TreeMap<Calendar, WeekMenus>());
        menusTreeMap.put(CafeteriaType.OREUM3, new TreeMap<Calendar, WeekMenus>());
    }

    public static MenuManager getInstance() {
        if(_instance == null)
            _instance = new MenuManager();
        return _instance;
    }

    public WeekMenus getMenus(CafeteriaType cafeteriaType, Calendar date, boolean isForceUpdate){
        // 찾아서 반환
        Log.d("aaaaa", "targetDate" + DateUtility.dateToString(date,'.') );
        WeekMenus result = find(cafeteriaType, date);
        if(result == null || isForceUpdate){
            // 없으면 업데이트
            update(cafeteriaType, date);
            result = find(cafeteriaType, date);
        }

        return result;
    }

    public boolean update(CafeteriaType cafeteriaType, Calendar date){
        try{
            WeekMenus weekMenus = new Parser().parse(cafeteriaType, date);
            menusTreeMap.get(cafeteriaType).put(weekMenus.getStartDate(), weekMenus);  // 트리의 키값은 시작일(월요일)임
            Log.d("aaaaa", "updated Date : " + DateUtility.dateToString(weekMenus.getStartDate(),'.') );
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private WeekMenus find(CafeteriaType cafeteriaType, Calendar date){
        TreeMap<Calendar, WeekMenus> weekMenusList = menusTreeMap.get(cafeteriaType);

        // 요청한 날짜가 포함된 주의 시작일 찾기
        Calendar startDate = containsWeek(cafeteriaType, date);
        if(startDate == null){
            return null;
        }

        // 그 주가 포함된 항목이 있으면 반환
        return weekMenusList.get(startDate);
    }


    private Calendar containsWeek(CafeteriaType cafeteriaType, Calendar date){
        TreeMap<Calendar, WeekMenus> weekMenusList = menusTreeMap.get(cafeteriaType);

        // 요청한 날짜가 포함된 주의 월요일(=시작일) 구하기
        Calendar startDate = (Calendar)date.clone();
        if(startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            // 일요일이면 하루 빼고 월요일 구해야댐.
            startDate.add(Calendar.DATE, -1);
        }
        startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);       // 월요일 구하기
        Log.d("aaaaa", "Monday Date : " + DateUtility.dateToString(startDate,'.') );

        for(Calendar c : weekMenusList.keySet()) {
            if(c.compareTo(startDate) == 0){
                return c;
            }
        }
        return null;
    }


}
