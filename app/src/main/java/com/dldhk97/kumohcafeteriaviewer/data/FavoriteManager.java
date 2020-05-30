package com.dldhk97.kumohcafeteriaviewer.data;

import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

import java.util.ArrayList;

public class FavoriteManager {
    private static FavoriteManager _instance;

    private static ArrayList<Item> currentItems = new ArrayList<>();

    private FavoriteManager() {
        sync();
    }

    public static FavoriteManager getInstance() {
        if(_instance == null)
            _instance = new FavoriteManager();
        return _instance;
    }

    public ArrayList<Item> getCurrentItems() {
        return currentItems;
    }

    private void sync(){
        currentItems = getAllItems();
    }

    public boolean addItem(final Item item){
        ArrayList<String> columns = getAllColumnsWithoutID();
        ArrayList<String> values = new ArrayList<String>() {{
            add(item.getItemName());
        }};

        if(findItem(item.getItemName()) != null){
            return false;
        }

        boolean isSucceed = DatabaseManager.getInstance().insert(DatabaseInfo.TABLE_FAVORITES.toString(), columns, values);
        sync();
        return isSucceed;
    }

    private ArrayList<Item> getAllItems(){
        ArrayList<String> columns = getAllColumnsWithoutID();
        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_FAVORITES.toString(), columns, null);
        if(received == null)
            return null;

        ArrayList<Item> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                String foodName = row.get(0);
                result.add(new Item(foodName, ItemType.FOOD));
            }
        }

        return result.size() > 0 ? result : null;
    }

    public Item findItem(String itemName){
        ArrayList<String> columns = getAllColumnsWithoutID();
        String select = DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString() + " = '" + itemName.replace("'","''") + "'";

        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_FAVORITES.toString(), columns, select);
        if(received == null)
            return null;

        ArrayList<Item> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                String foodName = row.get(0);
                return new Item(foodName, ItemType.FOOD);
            }
        }
        return null;
    }

    public void deleteItem(Item item){
        String s = item.getItemName().replace("'","''");
        String select = DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString() + " = '" + s + "'";

        DatabaseManager.getInstance().deleteRow(DatabaseInfo.TABLE_FAVORITES.toString(), select);
        sync();
    }

    private ArrayList<String> getAllColumnsWithoutID(){
        ArrayList<String> columns = new ArrayList<String>() {{
            add(DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString());
        }};
        return columns;
    }


}
