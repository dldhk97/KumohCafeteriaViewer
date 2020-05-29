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

    public boolean addFavorite(Item item){
        boolean isSucceed = DatabaseManager.getInstance().insert(item.getItemName());
        syncFavorites();
        return isSucceed;
    }

    private ArrayList<Item> getAllFavorites(){
        ArrayList<String> received = DatabaseManager.getInstance().getFavorites();

        if(received == null){
            return null;
        }

        ArrayList<Item> result = new ArrayList<>();
        for(String s : received){
            result.add(new Item(s, ItemType.FOOD));
        }
        return result.size() > 0 ? result : null;
    }

    public Item findFavorite(String itemName){
        String s = DatabaseManager.getInstance().findFavorite(itemName);
        if(s == null)
            return null;
        return new Item(s, ItemType.FOOD);
    }

    private void syncFavorites(){
        currentFavorites = getAllFavorites();
    }

    public void deleteFavorite(Item item){
        DatabaseManager.getInstance().deleteFavorite(item.getItemName());
        syncFavorites();
    }


}
