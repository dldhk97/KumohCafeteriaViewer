package com.dldhk97.kumohcafeteriaviewer.data;

import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

import java.util.ArrayList;

public class FavoriteManager {
    private static FavoriteManager _instance;

    private static ArrayList<Item> currentFavorites = new ArrayList<>();

    private FavoriteManager() {
        syncFavorites();
    }

    public static FavoriteManager getInstance() {
        if(_instance == null)
            _instance = new FavoriteManager();
        return _instance;
    }

    public ArrayList<Item> getCurrentFavorites() {
        return currentFavorites;
    }

    public boolean addFavorite(final Item item){
        ArrayList<String> columns = getAllColumns();
        ArrayList<String> values = new ArrayList<String>() {{
            add(item.getItemName());
        }};

        if(findFavorite(item.getItemName()) != null){
            return false;
        }

        boolean isSucceed = DatabaseManager.getInstance().insert(DatabaseInfo.TABLE_FAVORITES.toString(), columns, values);
        syncFavorites();
        return isSucceed;
    }

    private ArrayList<Item> getAllFavorites(){
        ArrayList<String> columns = getAllColumns();
        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_FAVORITES.toString(), columns, null);
        if(received == null)
            return null;

        ArrayList<Item> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                result.add(new Item(row.get(0), ItemType.FOOD));
            }
        }

        return result.size() > 0 ? result : null;
    }

    public Item findFavorite(String itemName){
        ArrayList<String> columns = getAllColumns();
        String select = DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString() + " = '" + itemName.replace("'","''") + "'";

        ArrayList<ArrayList<String>> received = DatabaseManager.getInstance().select(DatabaseInfo.TABLE_FAVORITES.toString(), columns, select);
        if(received == null)
            return null;

        ArrayList<Item> result = new ArrayList<>();
        for(ArrayList<String> row : received){
            if(row != null && row.size() > 0){
                result.add(new Item(row.get(0), ItemType.FOOD));
            }
        }
        return result.size() > 0 ? result.get(0) : null;
    }

    private void syncFavorites(){
        currentFavorites = getAllFavorites();
    }

    public void deleteFavorite(Item item){
        String s = item.getItemName().replace("'","''");
        String select = DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString() + " = '" + s + "'";

        DatabaseManager.getInstance().deleteRow(DatabaseInfo.TABLE_FAVORITES.toString(), select);
        syncFavorites();
    }

    private ArrayList<String> getAllColumns(){
        ArrayList<String> columns = new ArrayList<String>() {{
            add(DatabaseInfo.TABLE_FAVORITES_COLUMN_ITEMNAME.toString());
        }};
        return columns;
    }


}
