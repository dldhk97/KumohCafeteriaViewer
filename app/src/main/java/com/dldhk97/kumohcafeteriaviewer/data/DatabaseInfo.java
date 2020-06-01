package com.dldhk97.kumohcafeteriaviewer.data;

public enum DatabaseInfo {
    DB_NAME("KumohCafeteriaViewer.db"),
    DB_VERSION("4"),

    TABLE_FAVORITES("Favorites"),
    TABLE_FAVORITES_COLUMN_ID("_id"),
    TABLE_FAVORITES_COLUMN_ITEMNAME("ItemName"),

    TABLE_MENUS("Menus"),
    TABLE_MENUS_COLUMN_ID("_id"),
    TABLE_MENUS_COLUMN_DATE("Date"),
    TABLE_MENUS_COLUMN_CAFETERIA("Cafeteria"),
    TABLE_MENUS_COLUMN_MEALTIMETYPE("MealTimeType"),
    TABLE_MENUS_COLUMN_ISOPEN("IsOpen"),
    TABLE_MENUS_COLUMN_ITEMNAME("ItemName"),
    TABLE_MENUS_COLUMN_ITEMTYPE("ItemType"),


    TABLE_NOTIFICATIONITEMS("NotificationItem"),
    TABLE_NOTIFICATIONITEMS_COLUMN_ID("_id"),
    TABLE_NOTIFICATIONITEMS_COLUMN_NAME("Name"),
    TABLE_NOTIFICATIONITEMS_COLUMN_CAFETERIA("Cafeteria"),
    TABLE_NOTIFICATIONITEMS_COLUMN_MEALTIME("MealTime"),
    TABLE_NOTIFICATIONITEMS_COLUMN_HOUR("Hour"),
    TABLE_NOTIFICATIONITEMS_COLUMN_MIN("Min"),
    TABLE_NOTIFICATIONITEMS_COLUMN_ACTIVATED("Activated");



    private String val;

    private DatabaseInfo(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
