package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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

        // 파싱 테스트
        Parser p = new Parser();
        Calendar test = Calendar.getInstance();
        ArrayList<Menu> x = null;
        try {
            x = p.parse(cafeteriaType, test);
            for(Menu m : x){
                Log.d("DEBUG",m.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        final RecyclerView menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        CafeteriaRecyclerAdapter cra = new CafeteriaRecyclerAdapter(container.getContext(), x);
        menu_inner_recyclerView.setAdapter(cra);

        return root;
    }


}
