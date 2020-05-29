package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.Context;

public class DatabaseManager {
    private static DatabaseManager _instance;
    private Context context;

    private DatabaseManager() {}
    public void setContext(Context context) {this.context = context;}

    public static DatabaseManager getInstance() {
        if(_instance == null)
            _instance = new DatabaseManager();
        return _instance;
    }
}
