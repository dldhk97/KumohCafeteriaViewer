package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;

import java.util.ArrayList;

public class DatabaseManager {


    private static DatabaseManager _instance;
    private SQLiteDatabase mydatabase = null;
    private Context context;

    private DatabaseManager() {}

    public void setContextIfNotExist(Context context) {
        if(this.context == null)
            this.context = context;
        createTable();
    }

    public static DatabaseManager getInstance() {
        if(_instance == null){
            _instance = new DatabaseManager();
        }

        return _instance;
    }

    private void createTable(){
        try{
            if(context == null){
                throw new MyException(ExceptionType.CONTEXT_NOT_INITIALIZED, "DatabaseManager not initialized");
            }

            //DB Open
            mydatabase = context.openOrCreateDatabase(DatabaseInfo.DB_NAME.toString(), context.MODE_PRIVATE,null);

            // 버전 체크
            String version = loadVersion();
            if(!version.equals(DatabaseInfo.DB_VERSION)){
                deleteTable();
                Log.d("aaaaa", "Table deleted. Update table " + version + " -> " + DatabaseInfo.DB_VERSION + ".");
            }

            // Favorite Table 생성
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_FAVORITES +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME + " TEXT);");

            // Menus Table 생성
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_MENUS +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_DATE + " TEXT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_CAFETERIA + " TEXT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_MEALTIMETYPE + " TEXT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_ISOPEN + " TEXT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_ITEMNAME + " TEXT," +
                    DatabaseInfo.TABLE_MENUS_COLUMN_ITEMTYPE + " TEXT);");

            // Alarm Table 생성
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TABLE_NOTIFICATIONITEMS +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_NAME + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_CAFETERIA + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MEALTIME + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_HOUR + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_MIN + " TEXT," +
                    DatabaseInfo.TABLE_NOTIFICATIONITEMS_COLUMN_ACTIVATED + " TEXT);");

            saveVersion();
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert(e.getMessage());
        }

    }

    private void deleteTable(){
        mydatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.TABLE_FAVORITES);
        mydatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.TABLE_MENUS);
        mydatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.TABLE_NOTIFICATIONITEMS);
    }

    public boolean insert(String table , ArrayList<String> columns, ArrayList<String> values){
        if(columns.size() != values.size())
            return false;

        ContentValues addRowValue = new ContentValues();
        for(int i = 0 ; i < columns.size() ; i++){
            addRowValue.put(columns.get(i), values.get(i));
        }

        return mydatabase.insert(table, null, addRowValue) > 0 ? true : false;
    }

    public ArrayList<ArrayList<String>> select(String table, ArrayList<String> columns, String where){
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        String[] cols = new String[columns.size()];
        cols = columns.toArray(cols);
        Cursor cursor = mydatabase.query(table,
                cols,
                where,
                null,
                null,
                null,
                null);

        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                ArrayList<String> row = new ArrayList<>();
                for(int i = 0 ; i < columns.size() ; i++){
                    row.add(cursor.getString(i));
                }
                result.add(row);
            }
        }
        return result.size() > 0 ? result : null;
    }

    public boolean deleteRow(String table, String where){
        try{
            String sql = "delete from " + table + " where " + where;
            mydatabase.execSQL(sql);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updateItem(String table, String set,  String where){
        try{
            String sql = "update " + table + " set " + set + " where " + where;
            mydatabase.execSQL(sql);
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    private String PREFS_NAME = DatabaseInfo.DB_NAME + "_DB_VERSION";
    private String PREF_PREFIX_KEY = "DB_VERSION";
    private void saveVersion(){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY, String.valueOf(DatabaseInfo.DB_VERSION));        // 레이아웃 유형 저장(큰놈인지 아닌지)
        prefs.apply();
    }

    private String loadVersion(){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        final String version = prefs.getString(PREF_PREFIX_KEY, "0");
        return version;
    }
}
