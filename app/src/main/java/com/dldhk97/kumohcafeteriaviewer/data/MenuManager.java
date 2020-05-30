package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.Context;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;
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
    private static Context context;
    private TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> currentMenuMap;      // <음식점타입<시작일,주메뉴들>>

    private MenuManager(){
        currentMenuMap = new TreeMap<>();
        currentMenuMap.put(CafeteriaType.STUDENT, new TreeMap<Calendar, WeekMenus>());
        currentMenuMap.put(CafeteriaType.STAFF, new TreeMap<Calendar, WeekMenus>());
        currentMenuMap.put(CafeteriaType.SNACKBAR, new TreeMap<Calendar, WeekMenus>());
        currentMenuMap.put(CafeteriaType.PUROOM, new TreeMap<Calendar, WeekMenus>());
        currentMenuMap.put(CafeteriaType.OREUM1, new TreeMap<Calendar, WeekMenus>());
        currentMenuMap.put(CafeteriaType.OREUM3, new TreeMap<Calendar, WeekMenus>());
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
            currentMenuMap.get(cafeteriaType).put(weekMenus.getStartDate(), weekMenus);  // 트리의 키값은 시작일(월요일)임
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

            for(CafeteriaType cafeteriaType : currentMenuMap.keySet()){
                update(cafeteriaType, date);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // -----------------------------------------------

    // DB 관련 메소드

    public TreeMap<CafeteriaType, TreeMap<Calendar, WeekMenus>> getCurrentMenuMap() {
        return currentMenuMap;
    }

    // 한 식단을 등록함
    public boolean addMenuToDB(final Menu menu){
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

    private ArrayList<Menu> getAllMenusFromDB(){
        ArrayList<String> columns = getAllColumnsWithoutID();
        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_MENUS.toString(), columns, null);
        if(received == null)
            return null;

        ArrayList<Menu> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                try{
                    Calendar date = DateUtility.stringToDate(row.get(0));
                    CafeteriaType cafeteriaType = CafeteriaType.valueOf(row.get(1));
                    MealTimeType mealTimeType = MealTimeType.valueOf(row.get(2));
                    boolean isOpen = Boolean.parseBoolean(row.get(3));
                    Item i = new Item(row.get(4), ItemType.valueOf(row.get(5)));

                    result.add(new Menu(date, cafeteriaType, mealTimeType, isOpen));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return result.size() > 0 ? result : null;
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
