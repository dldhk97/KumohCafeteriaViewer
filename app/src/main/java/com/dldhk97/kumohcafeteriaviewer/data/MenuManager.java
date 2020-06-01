package com.dldhk97.kumohcafeteriaviewer.data;


import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;
import com.dldhk97.kumohcafeteriaviewer.model.DayMenus;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.parser.Parser;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;
import com.dldhk97.kumohcafeteriaviewer.utility.NetworkStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class MenuManager {
    private static MenuManager _instance;

    private TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> currentMenuMap;      // <음식점타입<시작일,주메뉴들>>

    private MenuManager(){
        sync();
    }

    private TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> initTreeMap(){
        TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> treeMap = new TreeMap<>();

        treeMap.put(CafeteriaType.STUDENT, new TreeMap<Calendar, WeekMenus>());
        treeMap.put(CafeteriaType.STAFF, new TreeMap<Calendar, WeekMenus>());
        treeMap.put(CafeteriaType.SNACKBAR, new TreeMap<Calendar, WeekMenus>());
        treeMap.put(CafeteriaType.PUROOM, new TreeMap<Calendar, WeekMenus>());
        treeMap.put(CafeteriaType.OREUM1, new TreeMap<Calendar, WeekMenus>());
        treeMap.put(CafeteriaType.OREUM3, new TreeMap<Calendar, WeekMenus>());
        return treeMap;
    }


    public static MenuManager getInstance() {
        if(_instance == null)
            _instance = new MenuManager();

        return _instance;
    }

    private void sync(){
        if(currentMenuMap != null){
            currentMenuMap.clear();
        }
        currentMenuMap = getAllItems();
        if(currentMenuMap == null){
            currentMenuMap = initTreeMap();
        }
    }

    public WeekMenus getMenus(CafeteriaType cafeteriaType, Calendar date, boolean isForceUpdate) throws Exception{
        // 로컬에 있는지 찾아서 반환
        WeekMenus result = find(cafeteriaType, date);
        if(result == null || isForceUpdate){
            // 없으면 업데이트하는데, 인터넷 연결안되있으면 걍 null 반환
            try{
                if(NetworkStatus.getCurrentStatus() != NetworkStatusType.CONNECTED){
                    return null;
                }
                parseAndUpdate(cafeteriaType, date);
            }
            catch (Exception e){
                e.printStackTrace();
                throw e;
            }

            result = find(cafeteriaType, date);
        }

        return result;
    }

    // 파싱해서 DB에 누적
    public boolean parseAndUpdate(CafeteriaType cafeteriaType, Calendar date) throws Exception{
        try{
            WeekMenus weekMenus = new Parser().parse(cafeteriaType, date);
            Log.d("aaaaa", "parsed");
            currentMenuMap.get(cafeteriaType).put(weekMenus.getStartDate(), weekMenus);  // 트리의 키값은 시작일(월요일)임
            addWeeksMenuToDB(weekMenus);
            sync();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private WeekMenus find(CafeteriaType cafeteriaType, Calendar date){
        TreeMap<Calendar, WeekMenus> weekMenusList = currentMenuMap.get(cafeteriaType);

        // 요청한 날짜가 포함된 주의 시작일 찾기
        Calendar startDate = containsWeek(cafeteriaType, date);
        if(startDate == null){
            return null;
        }

        // 그 주가 포함된 항목이 있으면 반환
        return weekMenusList.get(startDate);
    }


    public Calendar containsWeek(CafeteriaType cafeteriaType, Calendar date){
        TreeMap<Calendar, WeekMenus> weekMenusList = currentMenuMap.get(cafeteriaType);

        try{
            // 요청한 날짜가 포함된 주의 월요일(=시작일) 구하기
            Calendar startDate = (Calendar)date.clone();
            startDate = DateUtility.remainOnlyDate(startDate);
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
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    // -----------------------------------------------

    // DB 관련 메소드
    public TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> getCurrentMenuMap() {
        return currentMenuMap;
    }

    public boolean addWeeksMenuToDB(final WeekMenus weekMenus){
        try{
            for(DayMenus dayMenus : weekMenus.getDayMenuList()){
                for(Menu menu : dayMenus.getMenus()){
                    addMenuToDB(menu);
                }
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    // 한 식단을 등록함
    private boolean addMenuToDB(final Menu menu){
        if(menu == null)
            return false;
        int cnt = 0;
        for(Item i : menu.getItems()){
            if(addItemToDB(menu, i)){
                cnt++;
            }

        }
        return cnt == menu.getCount() ? true : false;
    }

    // 한 음식을 추가함.
    private boolean addItemToDB(final Menu menu, final Item item){

        // 중복 체크
        Menu receivedMenu = findMenu(menu);
        if(receivedMenu != null){
            for(Item i : receivedMenu.getItems()){
                if(i.getItemName().equals(item.getItemName())){
                    return false;
                }
            }
        }


        ArrayList<String> columns = getAllColumnsWithoutID();
        ArrayList<String> values = new ArrayList<String>() {{
            add(DateUtility.dateToString(menu.getDate(), '.'));
            add(menu.getCafeteriaType().toString());
            add(menu.getMealTimeType().toString());
            add(String.valueOf(menu.isOpen()));
            add(item.getItemName());
            add(item.getItemType().toString());
        }};

        boolean isSucceed = DatabaseManager.getInstance().insert(DatabaseInfo.TABLE_MENUS.toString(), columns, values);
        return isSucceed;
    }

    // DB의 모든 메뉴를 가져온다.
    private TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> getAllItems(){
        ArrayList<String> columns = getAllColumnsWithoutID();
        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_MENUS.toString(), columns, null);
        if(received == null)
            return null;

        TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> result = initTreeMap();

        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                try{
                    Calendar date = DateUtility.stringToDate(row.get(0));
                    CafeteriaType cafeteriaType = CafeteriaType.stringTo(row.get(1));
                    MealTimeType mealTimeType = MealTimeType.stringTo(row.get(2));
                    boolean isOpen = Boolean.parseBoolean(row.get(3));
                    Item item = new Item(row.get(4), ItemType.valueOf(row.get(5)));

                    // 주어진 날짜가 해당되는 주의 월요일(startDate) 찾기
                    int dayOfWeek =  date.get(Calendar.DAY_OF_WEEK);
                    int diff = 0;
                    if(dayOfWeek == Calendar.SUNDAY){
                        diff = -6;
                    }
                    else{
                        diff = -dayOfWeek + 2;
                    }
                    Calendar startDate = (Calendar)date.clone();
                    startDate.add(Calendar.DATE, diff);

                    // WeekMenu 존재 안하면 생성
                    WeekMenus weekMenus = result.get(cafeteriaType).get(startDate);
                    if(weekMenus == null){
                         weekMenus = new WeekMenus(startDate, cafeteriaType);
                        result.get(cafeteriaType).put(startDate, weekMenus);
                    }
                    else{
                        weekMenus = result.get(cafeteriaType).get(startDate);
                    }

                    // DayMenu 없으면 생성
                    DayMenus dayMenus = weekMenus.get(date);
                    if(dayMenus == null){
                        dayMenus = new DayMenus(date, cafeteriaType);
                        weekMenus.add(dayMenus);
                    }
                    else{
                        dayMenus = weekMenus.get(date);
                    }

                    // Menu 찾기
                    Menu menu = null;
                    for(Menu m : dayMenus.getMenus()){
                        if(m.getDate().compareTo(date) == 0 && m.getCafeteriaType().equals(cafeteriaType) && m.getMealTimeType().equals(mealTimeType)){
                            menu = m;
                            break;
                        }
                    }
                    // Menu 없으면 생성
                    if(menu == null){
                        menu = new Menu(date, cafeteriaType, mealTimeType, isOpen);
                        dayMenus.getMenus().add(menu);
                    }

                    // 아이템 삽입
                    menu.addItem(item);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return result.size() > 0 ? result : null;
    }

    public Menu findMenu(Menu menu){
        ArrayList<String> columns = getAllColumnsWithoutID();
        String select = DatabaseInfo.TABLE_MENUS_COLUMN_DATE.toString() + " = '" + menu.getDate().toString() + "' AND " +
                DatabaseInfo.TABLE_MENUS_COLUMN_CAFETERIA.toString() + " = '" + menu.getCafeteriaType().toString() + "' AND " +
                DatabaseInfo.TABLE_MENUS_COLUMN_MEALTIMETYPE.toString() + " = '" + menu.getMealTimeType().toString() + "'";

        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_MENUS.toString(), columns, select);
        if(received == null)
            return null;

        Menu result = new Menu(menu.getDate(), menu.getCafeteriaType(), menu.getMealTimeType(), menu.isOpen());
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                result.addItem(new Item(row.get(4), ItemType.valueOf(row.get(5))));
            }
        }
        return result.getItems().size() > 0 ? result : null;
    }

    private ArrayList<String> getAllColumnsWithoutID(){
        ArrayList<String> columns = new ArrayList<String>() {{
            add(DatabaseInfo.TABLE_MENUS_COLUMN_DATE.toString());
            add(DatabaseInfo.TABLE_MENUS_COLUMN_CAFETERIA.toString());
            add(DatabaseInfo.TABLE_MENUS_COLUMN_MEALTIMETYPE.toString());
            add(DatabaseInfo.TABLE_MENUS_COLUMN_ISOPEN.toString());
            add(DatabaseInfo.TABLE_MENUS_COLUMN_ITEMNAME.toString());
            add(DatabaseInfo.TABLE_MENUS_COLUMN_ITEMTYPE.toString());
        }};
        return columns;
    }


}
