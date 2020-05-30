package com.dldhk97.kumohcafeteriaviewer.enums;

public enum ItemType {
    FOOD, TIME, ETC, NONE, DIVIDER, PRICE, UNKNOWN;

    public ItemType stringTo(String string){
        switch(string){
            case "FOOD":
                return FOOD;
            case "TIME":
                return TIME;
            case "ETC":
                return ETC;
            case "NONE":
                return NONE;
            case "DIVIDER":
                return FOOD;
            case "PRICE":
                return PRICE;
            default:
                return UNKNOWN;
        }
    }
}
