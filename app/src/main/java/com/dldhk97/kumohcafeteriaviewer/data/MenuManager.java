package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.Context;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.parser.Parser;
import com.dldhk97.kumohcafeteriaviewer.utility.NetworkStatus;

import java.util.Calendar;
import java.util.TreeMap;

public class MenuManager {
    private static MenuManager _instance;
    private static Context context;
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

    public void setContext(Context context){
        this.context = context;
    }

    public WeekMenus getMenus(CafeteriaType cafeteriaType, Calendar date, boolean isForceUpdate) throws Exception{
        // 찾아서 반환
        WeekMenus result = find(cafeteriaType, date);
        if(result == null || isForceUpdate){
            // 없으면 업데이트하는데, 인터넷 연결안되있으면 걍 null 반환
            try{
                if(NetworkStatus.getCurrentStatus() != NetworkStatusType.CONNECTED){
                    return null;
                }
                update(cafeteriaType, date);
            }
            catch (Exception e){
                e.printStackTrace();
                throw e;
            }

            result = find(cafeteriaType, date);
        }

        return result;
    }

    public boolean update(CafeteriaType cafeteriaType, Calendar date) throws Exception{
        try{
            WeekMenus weekMenus = new Parser().parse(cafeteriaType, date);
            menusTreeMap.get(cafeteriaType).put(weekMenus.getStartDate(), weekMenus);  // 트리의 키값은 시작일(월요일)임
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
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


    public Calendar containsWeek(CafeteriaType cafeteriaType, Calendar date){
        TreeMap<Calendar, WeekMenus> weekMenusList = menusTreeMap.get(cafeteriaType);

        // 요청한 날짜가 포함된 주의 월요일(=시작일) 구하기
        Calendar startDate = (Calendar)date.clone();
        if(startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            // 일요일이면 하루 빼고 월요일 구해야댐.
            startDate.add(Calendar.DATE, -1);
        }
        startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);       // 월요일 구하기

        for(Calendar c : weekMenusList.keySet()) {
            if(c.compareTo(startDate) == 0){
                return c;
            }
        }
        return null;
    }

    public boolean preload(Calendar date){
        try{
            if(NetworkStatus.checkStatus(context) != NetworkStatusType.CONNECTED){
                return false;
            }

            for(CafeteriaType cafeteriaType : menusTreeMap.keySet()){
                update(cafeteriaType, date);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
