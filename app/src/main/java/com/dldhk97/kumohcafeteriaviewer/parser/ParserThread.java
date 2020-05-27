package com.dldhk97.kumohcafeteriaviewer.parser;

import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

public class ParserThread implements Runnable{
    private static final int TIMEOUT = 2000;
    private String url;
    private CafeteriaType cafeteriaType;
    private final int TRY_CNT = 2;

    private IParseCompleteListener parseCompleteListener;

    public ParserThread(String url, CafeteriaType cafeteriaType){
        this.url = url;
        this.cafeteriaType = cafeteriaType;
    }

    //파싱 메소드
    @Override
    public void run() {
        ArrayList<Menu> resultArr = new ArrayList<>();
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
                        parseCompleteListener.onParseComplete(ExceptionType.PARSE_FAILED, resultArr);
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
            Calendar startDate = DateUtility.StringToDate(startDateStr);

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
                Menu currentMenu = new Menu(currentMenuDate, cafeteriaType, mealTimeType);
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
                }

                //전달할 배열에 추가
                resultArr.add(currentMenu);
                cnt++;
            }

            // 파싱 완료 알리고 배열 전달
            parseCompleteListener.onParseComplete(null, resultArr);
        }
        catch(Exception e){
            Log.d("[ParseThread.run]\n","Parse Failed\n" + e.getMessage());
            parseCompleteListener.onParseComplete(ExceptionType.PARSE_FAILED, resultArr);
        }

    }

    // 항목 이름으로 음식/가격/시간/기타 등으로 구별
    private ItemType getItemType(String itemName){

        return ItemType.FOOD;
    }



    // 데이터 전달을 위한 리스너 설정
    public void setOnParseCompleteReceivedEvent(IParseCompleteListener listener){
        parseCompleteListener = listener;
    }

    // 데이터 전달을 위한 인터페이스
    public interface IParseCompleteListener {
        void onParseComplete(ExceptionType exceptionType, ArrayList<Menu> parsedArr);
    }
}
