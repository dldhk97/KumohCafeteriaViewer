package com.dldhk97.kumohcafeteriaviewer.data;

import android.content.Context;

import com.dldhk97.kumohcafeteriaviewer.model.Item;

public class FavoriteManager {
    private static FavoriteManager _instance;

    private FavoriteManager() {}

    public static FavoriteManager getInstance() {
        if(_instance == null)
            _instance = new FavoriteManager();
        return _instance;
    }

    public void addFavorite(Item item){

    }


}
