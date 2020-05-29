package com.dldhk97.kumohcafeteriaviewer.utility;

import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtility {
    // 항목 이름으로 음식/가격/시간/기타 등으로 항목의 타입 구별
    public static ItemType getItemType(String itemName){
        Pattern pattern = Pattern.compile("([1-9]|[01][0-9]|2[0-3]):([0-5][0-9])");
        Matcher matcher = pattern.matcher(itemName);
        if(matcher.find()){
            return ItemType.TIME;
        }
        if(itemName.startsWith("[") && itemName.endsWith("]")){
            return ItemType.ETC;
        }
        if(itemName.matches("-*")){
            return ItemType.DIVIDER;
        }
        if(itemName.contains("식당 운영 없음") || itemName.contains("식사정보 없음")){
            return ItemType.ETC;
        }
        return ItemType.FOOD;
    }
}
