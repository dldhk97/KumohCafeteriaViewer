package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {

    private ArrayList<InnerFragment> pages;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViewPager(root, container);
        initializeBottomSheet(root);

        return root;
    }

    private void initializeInnerFrags(){
        pages = new ArrayList<>(Arrays.asList(
                new InnerFragment(CafeteriaType.STUDENT),
                new InnerFragment(CafeteriaType.STAFF),
                new InnerFragment(CafeteriaType.SNACKBAR),
                new InnerFragment(CafeteriaType.PUROOM),
                new InnerFragment(CafeteriaType.OREUM1),
                new InnerFragment(CafeteriaType.OREUM3)
        ));
    }

    private void initializeViewPager(View root, ViewGroup container){
        // 탭레이아웃 찾기
        final TabLayout tabLayout = root.findViewById(R.id.main_tabLayout);

        // 프래그먼트 생성 후 탭 생성 및 추가
        initializeInnerFrags();
        for(InnerFragment frag : pages){
            tabLayout.addTab(tabLayout.newTab().setText(frag.getCafeteriaType().toString()));
        }

        FragmentActivity fa = (FragmentActivity)container.getContext();

        // TabLayout과 ViewPager 연결
        final ViewPager viewPager = root.findViewById(R.id.main_viewPager);
        InnerPagerAdapter innerPagerAdapter = new InnerPagerAdapter(fa.getSupportFragmentManager(), pages);
        viewPager.setAdapter(innerPagerAdapter);

        // 리스너 등록
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initializeBottomSheet(View root){
        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet));
        final TextView bottom_sheet_nowdate = root.findViewById(R.id.bottom_sheet_nowdate);
        bottom_sheet_nowdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else{
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        // 달력 바인딩
//        final DatePicker bottom_sheet_datepicker = root.findViewById(R.id.bottom_sheet_datepicker);
//        bottom_sheet_datepicker.
    }
}

