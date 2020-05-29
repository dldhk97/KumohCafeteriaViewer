package com.dldhk97.kumohcafeteriaviewer.parser;

import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.DayMenus;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class ParserThread implements Runnable{
    private static final int TIMEOUT = 2000;
    private String url;
    private CafeteriaType cafeteriaType;
    private final int TRY_CNT = 1;

    private IParseCompleteListener parseCompleteListener;

    public ParserThread(String url, CafeteriaType cafeteriaType){
        this.url = url;
        this.cafeteriaType = cafeteriaType;
    }

    //파싱 메소드
    @Override
    public void run() {
        try{
            Connection connection = Jsoup.connect(url);
            connection.timeout(TIMEOUT);
            Document doc = null;

            // n번까지 파싱 시도해보고 안되면 실패
            int tryCnt = TRY_CNT;
            while(tryCnt-- > 0){
                try{
                    doc = connection.get();
                    if(doc != null)
                        break;
                }
                catch (Exception e){
                    Log.d("[ParseThread.run]\n","connection.get()\n" + e.getMessage());
                    if(tryCnt <= 0){
                        parseCompleteListener.onParseComplete(ExceptionType.PARSE_FAILED, null);
                        return;
                    }
                }
            }


            // 등록된 메뉴가 있는지 체크
            Elements existCheck = doc.select("table > tbody > td");
            if (existCheck == null){
                throw new MyException(ExceptionType.MENU_NOT_EXIST, "등록된 메뉴가 없음");
            }

            // 이번주의 시작 날짜 겟
            Elements startDateHtml = doc.select("fieldset > div > div > p");
            String startDateStr = startDateHtml.get(0).text().replace("\t","").replace("\n","").replace("\r","");
            startDateStr = startDateStr.split("~")[0].trim();
            Calendar startDate = DateUtility.stringToDate(startDateStr);

            WeekMenus weekMenus = new WeekMenus(startDate, cafeteriaType);

            // 이번 주 모든 메뉴 겟
            Elements menuListHtml = doc.select("table > tbody > tr > td");
            int cnt = 0;
            for(Element menuHtml : menuListHtml){

                // 식사시간 알아내기
                MealTimeType mealTimeType = MealTimeType.UNKNOWN;
                if(this.cafeteriaType == CafeteriaType.SNACKBAR){   // 분식이면 식사시간을 일품요리로 고정
                    mealTimeType = MealTimeType.ONECOURSE;
                }
                else{
                    try{
                        String mealTimeStr = menuHtml.select("p").get(0).text();
                        mealTimeType = MealTimeType.strToValue(mealTimeStr);
                    }
                    catch (Exception e){
                        // 등록된 메뉴를 알 수 없거나 없을 때
                        mealTimeType = MealTimeType.UNKNOWN;
                    }
                }

                // 현재 메뉴의 날짜 설정
                Calendar currentMenuDate = Calendar.getInstance();
                currentMenuDate.setTime(startDate.getTime());
                currentMenuDate.add(Calendar.DATE, cnt % 7);        // 윗줄 7개는 중식이고, 아랫줄 7개는 석식이다.

                // 메뉴 객체 생성
                Menu currentMenu = new Menu(currentMenuDate, cafeteriaType, mealTimeType, false);
                if(mealTimeType != MealTimeType.UNKNOWN){
                    // 항목 파싱 후 메뉴에 삽입
                    Elements itemDetailHtml = menuHtml.select("ul > li");
                    for(Element itemElemHtml : itemDetailHtml){
                        String itemName = itemElemHtml.text();
                        ItemType itemType = getItemType(itemName);
                        currentMenu.addItem(new Item(itemName, itemType));
                    }
                }

                if(currentMenu.getItems().size() <= 0){
                    currentMenu.addItem(new Item("등록된 메뉴가 없습니다.", ItemType.NONE));
                    currentMenu.setIsOpen(false);
                }
                else if(currentMenu.getItem(0).getItemName().contains("식당 운영 없음")){
                    currentMenu.setIsOpen(false);
                }
                else{
                    currentMenu.setIsOpen(true);
                }

                //전달할 배열에 추가
                addMenu(weekMenus, currentMenu);
                cnt++;
            }

            // 파싱 완료 알리고 배열 전달
            parseCompleteListener.onParseComplete(null, weekMenus);
        }
        catch(Exception e){
            Log.d("[ParseThread.run]\n","Parse Failed\n" + e.getMessage());
            parseCompleteListener.onParseComplete(ExceptionType.PARSE_FAILED, null);
        }

    }

    // 항목 이름으로 음식/가격/시간/기타 등으로 항목의 타입 구별
    private ItemType getItemType(String itemName){
//        Pattern pattern = Pattern.compile("([1-9]|[01][0-9]|2[0-3]):([0-5][0-9])");
//        Matcher matcher = pattern.matcher(itemName);
//        if(matcher.find()){
//            return ItemType.TIME;
//        }
//        if(itemName.startsWith("[") && itemName.endsWith("]")){
//            return ItemType.ETC;
//        }
//        if(itemName.matches("-*")){
//            return ItemType.UNKNOWN;
//        }
        return ItemType.FOOD;
    }


    private void addMenu(WeekMenus weekMenus, Menu menu){
        Calendar currentMenuDate = menu.getDate();
        if(weekMenus.contains(currentMenuDate)){
            DayMenus dm = weekMenus.get(currentMenuDate);

            if(dm == null)
                dm = new DayMenus(currentMenuDate, menu.getCafeteriaType());

            dm.getMenus().add(menu);
        }
        else{
            DayMenus dm = new DayMenus(currentMenuDate, menu.getCafeteriaType());
            dm.getMenus().add(menu);
            weekMenus.add(dm);
        }
    }



    // 데이터 전달을 위한 리스너 설정
    public void setOnParseCompleteReceivedEvent(IParseCompleteListener listener){
        parseCompleteListener = listener;
    }

    // 데이터 전달을 위한 인터페이스
    public interface IParseCompleteListener {
        void onParseComplete(ExceptionType exceptionType, WeekMenus parsedArr);
    }
}
