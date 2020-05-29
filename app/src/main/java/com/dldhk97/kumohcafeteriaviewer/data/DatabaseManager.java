package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;

import java.util.ArrayList;

public class DatabaseManager {
    static final String DB_KUMOH = "KumohCafeteriaViewer.db"; //DB이름
    static final String TABLE_FAVORITES = "Favorites"; //Table 이름
    static final String KEY_COLUMN = "ItemName";

    static final int DB_VERSION = 1;

    private static DatabaseManager _instance;
    private SQLiteDatabase mydatabase = null;
    private Context context;

    private DatabaseManager() {}

    public void setContext(Context context) {
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
            mydatabase = context.openOrCreateDatabase(DB_KUMOH, context.MODE_PRIVATE,null);

            //Table 생성
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES +
                    "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_COLUMN + " TEXT);");
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert(e.getMessage());
        }

    }

    public boolean insert(String itemName){
        ContentValues addRowValue = new ContentValues();
        addRowValue.put(KEY_COLUMN, itemName);

        return mydatabase.insert(TABLE_FAVORITES, null, addRowValue) > 0 ? true : false;
    }

    public ArrayList<String> getFavorites(){
        ArrayList<String> result = new ArrayList<>();

        String[] columns = new String[] {KEY_COLUMN};
        Cursor cursor = mydatabase.query(TABLE_FAVORITES,
                columns,
                null,
                null,
                null,
                null,
                null);

        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                String currentData;
                currentData = cursor.getString(0);
                result.add(currentData);
            }
        }
        return result.size() > 0 ? result : null;
    }

    public String findFavorite(String itemName){
        String[] columns = new String[] {KEY_COLUMN};
        Cursor cursor = null;
        try{
            cursor = mydatabase.query(TABLE_FAVORITES,
                    columns,
                    KEY_COLUMN + " = '" + itemName + "'",
                    null,
                    null,
                    null,
                    null);
        }
        catch(Exception e){
            e.printStackTrace();
        }


        if(cursor != null)
        {
            while (cursor.moveToNext())
            {
                return cursor.getString(0);
            }
        }
        return null;
    }

    public boolean deleteFavorite(String itemName){
        String sql = "delete from " + TABLE_FAVORITES + " where " + KEY_COLUMN + " = '" + itemName + "';";
        mydatabase.execSQL(sql);
        return false;
    }
}
