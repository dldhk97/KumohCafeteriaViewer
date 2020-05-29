package com.dldhk97.kumohcafeteriaviewer.model;

import androidx.annotation.NonNull;

import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;

import java.io.Serializable;

public class Item implements Serializable {

    String itemName;
    ItemType itemType;

    public Item(String itemName, ItemType itemType){
        this.itemName = itemName;
        this.itemType = itemType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public String toString() {
        return itemName + "." + itemType.toString();
    }
}
