package com.dldhk97.kumohcafeteriaviewer.ui.home;

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

import java.util.Calendar;
import java.util.HashMap;

public class InnerFragment extends Fragment {
    private HashMap<Calendar, DayMenus> weekMenus;
    private final CafeteriaType cafeteriaType;

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
        final RecyclerView menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        // 날짜 특정해서 해당 날짜 메뉴 있으면 get
        Calendar x = Calendar.getInstance();
        DayMenus dm = null;
        try {
            dm = weekMenus.get(DateUtility.remainOnlyDate(x));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dm == null){
            dm = new DayMenus(x, CafeteriaType.UNKNOWN);
            dm.getMenus().add(new Menu(x, CafeteriaType.UNKNOWN, MealTimeType.UNKNOWN, false));
        }

        CafeteriaRecyclerAdapter cra = new CafeteriaRecyclerAdapter(container.getContext(), dm);
        menu_inner_recyclerView.setAdapter(cra);

        return root;
    }

    private void updateMenus(Calendar date){
        // 파싱 테스트
        Parser parser = new Parser();
        try {
            weekMenus = parser.parse(cafeteriaType, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
