package com.dldhk97.kumohcafeteriaviewer.parser;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;
import com.dldhk97.kumohcafeteriaviewer.utility.ResourceUtility;

import java.util.Calendar;

public class Parser{
    private WeekMenus resultArr;
    private ExceptionType resultException;

    // 해당 식당의 해당 날짜가 포함된 일주일치 식단을 파싱
    public WeekMenus parse(CafeteriaType cafeteriaType, Calendar date) throws Exception{
        if(cafeteriaType == CafeteriaType.UNKNOWN){
            throw new MyException(ExceptionType.UNKNOWN_CAFETERIA_TYPE, "Unknown current cafeteria type");
        }

        // url 설정
        ResourceUtility ru = new ResourceUtility();
        String url = cafeteriaType.getURL();

        // 날짜가 일요일이면 하루 뺀다.
        // 일요일이면 웹페이지에서 하루 넘어가기 때문임.
        if(date.get(Calendar.DAY_OF_WEEK) == 1){
            date.add(Calendar.DATE, -1);
        };

        // 해당 날짜로 설정
        url += "mode=menuList&srDt=" + DateUtility.dateToString(date, '-');

        // 학교 홈페이지에서 파싱
        ParserThread pt = new ParserThread(url, cafeteriaType);
        try{
            pt.setOnParseCompleteReceivedEvent(new ParseCompleteListener());        // 파싱 끝났을 때를 위한 이벤트 등록
            Thread thread = new Thread(pt);
            thread.start();
            thread.join();              // 파싱 끝날때까지 대기
        }
        catch(Exception e){
            throw new MyException(ExceptionType.PARSE_FAILED, "Parse failed.");
        }

        if(resultException != null){
            switch (resultException){
                case NETWORK_DISCONNECTED:
                    throw new MyException(resultException, "금오공과대학교 홈페이지 연결에 실패했습니다!");
                default:
                    throw new MyException(resultException, "식단표 파싱에 실패했습니다!");
            }

        }

        if(resultArr == null){
            return null;
        }

        return resultArr;
    }

    // 데이터 수신을 위한 리스너
    private class ParseCompleteListener implements ParserThread.IParseCompleteListener {

        // 데이터 수신
        @Override
        public void onParseComplete(ExceptionType exceptionType, WeekMenus parsedArr) {
            if(exceptionType != null){
                resultArr = null;
                resultException = exceptionType;
            }
            else{
                resultArr = parsedArr;
            }
        }
    }
}