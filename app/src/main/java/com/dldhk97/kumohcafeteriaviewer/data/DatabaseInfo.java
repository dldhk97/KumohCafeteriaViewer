package com.dldhk97.kumohcafeteriaviewer.data;

public enum DatabaseInfo {
    DB_NAME("KumohCafeteriaViewer.db"),
    DB_VERSION("2"),

    TABLE_FAVORITES("Favorites"),
    TABLE_FAVORITES_COLUMN_ITEMNAME("ItemName"),

    TABLE_MENUS("Menus"),
    TABLE_MENUS_COLUMN_ITEMNAME("ItemName"),

    TABLE_ALARMS("Alarms"),
    TABLE_ALARMS_COLUMN_NAME("Name"),
    TABLE_ALARMS_COLUMN_MEALTIME("MealTime"),
    TABLE_ALARMS_COLUMN_CAFETERIA("Cafeteria"),
    TABLE_ALARMS_COLUMN_TIME("Time"),
    TABLE_ALARMS_COLUMN_ACTIVATED("Activated");



    private String val;

    private DatabaseInfo(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
