package com.dldhk97.kumohcafeteriaviewer.data;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

import java.util.ArrayList;

public class NotificationItemManager {
    private static NotificationItemManager _instance;

    private static ArrayList<NotificationItem> currentItems = new ArrayList<>();

    private NotificationItemManager() {
        sync();
    }

    public static NotificationItemManager getInstance(){
        if(_instance == null)
            _instance = new NotificationItemManager();
        return _instance;
    }

    public ArrayList<NotificationItem> getCurrentItems() {
        return currentItems;
    }

    private void sync(){
        currentItems = getAllItems();
    }

    public boolean addItem(final NotificationItem notificationItem){
        ArrayList<String> columns = getAllColumnsWithoutID();

        ArrayList<String> values = itemToStringArrayWithOutID(notificationItem);

        if(findItem(notificationItem) != null){
            return false;
        }

        boolean isSucceed = DatabaseManager.getInstance().insert(DatabaseInfo.TABLE_NOTIFICATIONITEMS.toString(), columns, values);
        sync();
        return isSucceed;
    }

    // 식사 알람은 자동부여된 ID도 가져옴.
    private ArrayList<NotificationItem> getAllItems(){
        ArrayList<String> columns = getAllColumnsWithID();
        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_NOTIFICATIONITEMS.toString(), columns, null);
        if(received == null)
            return null;

        ArrayList<NotificationItem> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                result.add(stringArrayToItem(row));
            }
        }

        return result.size() > 0 ? result : null;
    }

    // 식사 알람은 id로 찾는다.
    public NotificationItem findItem(NotificationItem notificationItem){
        if(notificationItem.getId() == null){
            return null;
        }
        ArrayList<String> columns = getAllColumnsWithID();
        String select = DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ID.toString() + " = '" + notificationItem.getId() + "'";

        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_NOTIFICATIONITEMS.toString(), columns, select);
        if(received == null)
            return null;

        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                return stringArrayToItem(row);
            }
        }
        return null;
    }

    public boolean deleteItem(NotificationItem notificationItem){
        String id = notificationItem.getId();
        if(id == null){
            UIHandler.getInstance().showToast("NotificationItem delete failed! ID is NULL.");
            return false;
        }
        String where = DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ID.toString() + " = '" + id + "'";

        Boolean isSucceed = DatabaseManager.getInstance().deleteRow(DatabaseInfo.TABLE_NOTIFICATIONITEMS.toString(), where);
        sync();
        return isSucceed;
    }

    public boolean updateItem(NotificationItem notificationItem){
        if(findItem(notificationItem) == null){
            return false;
        }

        ArrayList<String> columns = getAllColumnsWithID();
        ArrayList<String> values =  itemToStringArrayWithID(notificationItem);

        StringBuilder setBuilder = new StringBuilder();
        for(int i = 0 ; i < columns.size() ; i++){
            setBuilder.append(columns.get(i) +" = '" + values.get(i).replace("'","''") + "', ");
        }
        setBuilder.delete(setBuilder.length()-2, setBuilder.length());
        String setSQL = setBuilder.toString();

        String id = notificationItem.getId();
        String where = DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ID.toString() + " = '" + id + "'";

        Boolean isSucceed = DatabaseManager.getInstance().updateItem(DatabaseInfo.TABLE_NOTIFICATIONITEMS.toString(), setSQL, where);
        sync();

        return isSucceed;
    }

    private ArrayList<String> getAllColumnsWithoutID(){
        ArrayList<String> columns = new ArrayList<String>() {{
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_NAME.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_CAFETERIA.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MEALTIME.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_HOUR.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MIN.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ACTIVATED.toString());
        }};
        return columns;
    }

    private ArrayList<String> getAllColumnsWithID(){
        ArrayList<String> columns = new ArrayList<String>() {{
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ID.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_NAME.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_CAFETERIA.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MEALTIME.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_HOUR.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MIN.toString());
            add(DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ACTIVATED.toString());
        }};
        return columns;
    }

    private NotificationItem stringArrayToItem(final ArrayList<String> array){
        String id = array.get(0);
        String name = array.get(1);
        CafeteriaType cafeteriaType = CafeteriaType.stringTo(array.get(2));
        MealTimeType mealTimeType = MealTimeType.stringTo(array.get(3));
        int hour = Integer.parseInt(array.get(4));
        int min = Integer.parseInt(array.get(5));
        boolean activated = Boolean.parseBoolean(array.get(6));

        return new NotificationItem(id, name, cafeteriaType, mealTimeType, hour, min, activated);
    }

    private ArrayList<String> itemToStringArrayWithID(final NotificationItem item){
        ArrayList<String> array = new ArrayList<String>() {{
            add(item.getId());
            add(item.getName());
            add(item.getCafeteriaType().toString());
            add(item.getMealTimeType().toString());
            add(String.valueOf(item.getHour()));
            add(String.valueOf(item.getMin()));
            add(String.valueOf(item.isActivated()));
        }};

        return array;
    }

    private ArrayList<String> itemToStringArrayWithOutID(final NotificationItem item){
        ArrayList<String> array = new ArrayList<String>() {{
            add(item.getName());
            add(item.getCafeteriaType().toString());
            add(item.getMealTimeType().toString());
            add(String.valueOf(item.getHour()));
            add(String.valueOf(item.getMin()));
            add(String.valueOf(item.isActivated()));
        }};

        return array;
    }

}
