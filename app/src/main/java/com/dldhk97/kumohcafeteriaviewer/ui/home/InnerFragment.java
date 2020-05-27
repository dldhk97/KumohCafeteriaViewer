package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.DayMenus;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.parser.Parser;
import com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView.CafeteriaRecyclerAdapter;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class InnerFragment extends Fragment {
    private HashMap<Calendar, DayMenus> weekMenus;
    private final CafeteriaType cafeteriaType;
    private Calendar currentDate;
    private ArrayList<Menu> currentMenus = null;

    private CafeteriaRecyclerAdapter cafeteriaRecyclerAdapter;
    private RecyclerView menu_inner_recyclerView;

    private Context tempCon;

    public InnerFragment(CafeteriaType cafeteriaType){
        this.cafeteriaType = cafeteriaType;
    }

    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_inner, container, false);

        if(weekMenus == null){
            Calendar today = Calendar.getInstance();
            updateMenus(today);
        }

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        cafeteriaRecyclerAdapter = new CafeteriaRecyclerAdapter(container.getContext(), currentMenus);
        tempCon = container.getContext();
        menu_inner_recyclerView.setAdapter(cafeteriaRecyclerAdapter);

        return root;
    }

    public void updateMenus(Calendar date){
        try {
            // 날짜만 남긴다. 시간은 제외
            currentDate = DateUtility.remainOnlyDate(date);

            // 주어진 날짜의 식단이 존재하지 않으면 파싱
            if(!isMenuExists(currentDate)){
                weekMenus = new Parser().parse(cafeteriaType, currentDate);
            }
            if(currentMenus!= null)
                currentMenus.clear();

            // 다시 주어진 날짜의 식단이 존재하는지 체크
            if(isMenuExists(currentDate)){
                // 있으면 메뉴 넣음.
                currentMenus = weekMenus.get(currentDate).getMenus();
            }
            else{
                // 없으면 없다는 메뉴를 임시로 만들어 넣음.
                currentMenus.add(new Menu(currentDate, CafeteriaType.UNKNOWN, MealTimeType.UNKNOWN, false));
            }
            cafeteriaRecyclerAdapter = new CafeteriaRecyclerAdapter(tempCon, currentMenus);
            menu_inner_recyclerView.setAdapter(cafeteriaRecyclerAdapter);
            cafeteriaRecyclerAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 주어진 날짜의 식단이 존재하는지 체크
    private boolean isMenuExists(Calendar date) throws Exception {
        // 날짜 특정해서 해당 날짜 메뉴 있으면 get
        if(weekMenus == null)
            return false;
        currentDate = DateUtility.remainOnlyDate(date);
        return weekMenus.containsKey(currentDate);
    }


}
