package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.model.WeekMenus;
import com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView.CafeteriaRecyclerAdapter;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

import java.util.ArrayList;
import java.util.Calendar;

public class InnerFragment extends Fragment {
    private WeekMenus weekMenus;
    private final CafeteriaType cafeteriaType;
    private Calendar currentDate = Calendar.getInstance();
    private ArrayList<Menu> currentMenus = null;

    private CafeteriaRecyclerAdapter cafeteriaRecyclerAdapter;
    private RecyclerView menu_inner_recyclerView;

    public InnerFragment(CafeteriaType cafeteriaType){
        this.cafeteriaType = cafeteriaType;
    }

    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home_inner, container, false);

        updateMenus(currentDate);

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        cafeteriaRecyclerAdapter = new CafeteriaRecyclerAdapter(container.getContext(), currentMenus);
        menu_inner_recyclerView.setAdapter(cafeteriaRecyclerAdapter);

        return root;
    }

    // 디스플레이에 보이는 메뉴 업데이트
    public void newUpdateMenus(Calendar date){
        try{
            // 요청한 날짜로 세팅
            currentDate = DateUtility.remainOnlyDate(date);

            // MenuManager한테 업데이트 요청.
            MenuManager.getInstance().getMenus(cafeteriaType, currentDate, false);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    // 날짜 변경 요청 시 호출됨. 리사이클러 뷰 내 항목 업데이트함.
    public void updateMenus(Calendar date){
        try {
            // 날짜만 남긴다. 시간은 제외
            currentDate = DateUtility.remainOnlyDate(date);

            // 주어진 날짜의 식단이 존재하지 않으면 파싱
            weekMenus = MenuManager.getInstance().getMenus(cafeteriaType, currentDate, false);

            // 다시 주어진 날짜의 식단이 존재하는지 체크
            if(isMenuExists(currentDate)){
                // 있으면 메뉴 넣음.

                currentMenus = weekMenus.get(currentDate).getMenus();
            }
            else{
                // 없으면 없다는 메뉴를 임시로 만들어 넣음.
                if(currentMenus != null){
                    currentMenus.clear();
                }
                else{
                    currentMenus = new ArrayList<>();
                }

                Menu emptyMenu = new Menu(currentDate, cafeteriaType, MealTimeType.UNKNOWN, false);
                emptyMenu.addItem(new Item("식사정보 없음", ItemType.FOOD));
                currentMenus.add(emptyMenu);
            }

            cafeteriaRecyclerAdapter.updateData(currentMenus);
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

        if(weekMenus.getCafeteriaType() != cafeteriaType){
            return false;
        }

        ArrayList<Menu> menus = null;
        if(weekMenus.contains(currentDate)){
            menus = weekMenus.get(currentDate).getMenus();
            if(menus != null){
                if(menus.size() > 0){
                    return true;
                }
            }
        }
        return false;
    }
}
