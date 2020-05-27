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
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.parser.Parser;
import com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView.CafeteriaRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class InnerFragment extends Fragment {
    private ArrayList<Menu> menus;
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

        if(menus == null){
            Calendar today = Calendar.getInstance();
            updateMenus(today);
        }

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        final RecyclerView menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        CafeteriaRecyclerAdapter cra = new CafeteriaRecyclerAdapter(container.getContext(), menus);
        menu_inner_recyclerView.setAdapter(cra);

        return root;
    }

    private void updateMenus(Calendar date){
        // 파싱 테스트
        Parser parser = new Parser();
        try {
            menus = parser.parse(cafeteriaType, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
