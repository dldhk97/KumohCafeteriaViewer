package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;
import com.dldhk97.kumohcafeteriaviewer.utility.NetworkStatus;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private ArrayList<InnerFragment> pages;

    private Calendar currentDate;

    TextView bottom_sheet_nowdate;
    DatePicker bottom_sheet_datepicker;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            currentDate = DateUtility.remainOnlyDate(Calendar.getInstance());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            initializeViewPager(root, container);
            initializeBottomSheet(root);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    private void initializeInnerFrags(ViewGroup container){
        pages = new ArrayList<>(Arrays.asList(
                new InnerFragment(CafeteriaType.STUDENT, this),
                new InnerFragment(CafeteriaType.STAFF, this),
                new InnerFragment(CafeteriaType.SNACKBAR, this),
                new InnerFragment(CafeteriaType.PUROOM, this),
                new InnerFragment(CafeteriaType.OREUM1, this),
                new InnerFragment(CafeteriaType.OREUM3, this)
        ));
    }

    private void initializeViewPager(View root, ViewGroup container){
        // 탭레이아웃 찾기
        final TabLayout tabLayout = root.findViewById(R.id.main_tabLayout);

        // 프래그먼트 생성 후 탭 생성 및 추가
        initializeInnerFrags(container);
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

        // ViewPager 기본 탭으로 설정한 것으로 선택
        String default_cafeteria = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("default_cafeteria", "학생식당");
        CafeteriaType defaultCafeteriaType = CafeteriaType.stringTo(default_cafeteria);
        for(int i = 0 ; i < pages.size() ; i++){
            if(pages.get(i).getCafeteriaType() == defaultCafeteriaType){
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    private void initializeBottomSheet(View root) throws Exception {
        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(root.findViewById(R.id.bottomSheet));
        bottom_sheet_nowdate = root.findViewById(R.id.bottom_sheet_currentDate);

        // 선택된 날짜 텍스트뷰 설정
        setBottomSheetNowDate(currentDate);
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

        // 달력 세팅
        bottom_sheet_datepicker = root.findViewById(R.id.bottom_sheet_datepicker);
        bottom_sheet_datepicker.init(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day_of_month) {
                String s = String.valueOf(year) + "." + String.valueOf(month + 1) + "." + String.valueOf(day_of_month);
                try {
                    currentDate = DateUtility.stringToDate(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateCurrentDateView(false);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        // 좌우 버튼 설정
        final ImageButton bottom_sheet_date_left = root.findViewById(R.id.bottom_sheet_date_left);
        final ImageButton bottom_sheet_date_right = root.findViewById(R.id.bottom_sheet_date_right);

        bottom_sheet_date_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.add(Calendar.DATE, -1);
                bottom_sheet_datepicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            }
        });
        bottom_sheet_date_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentDate.add(Calendar.DATE, 1);
                bottom_sheet_datepicker.updateDate(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    // 선택한 날짜가 변경되었을 때
    public void updateCurrentDateView(boolean isForceUpdate){
        try{
            setBottomSheetNowDate(currentDate);
            // 이번 날짜가 포함되있는지 체크하고, 포함안되있으면 네트워크 테스트
            if(MenuManager.getInstance().containsWeek(CafeteriaType.STUDENT, currentDate) == null){
                if(NetworkStatus.checkStatus(this.getContext()) != NetworkStatusType.CONNECTED){
                    UIHandler.getInstance().showToast("금오공과대학교 홈페이지 연결에 실패했습니다!");
                }
            }
            for(InnerFragment frag : pages){
                frag.updateMenus(currentDate, isForceUpdate);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setBottomSheetNowDate(Calendar date){
        String dayOfWeek = DateUtility.getDayOfWeek(date);
        String dateStr = DateUtility.dateToString(date, '.');
        bottom_sheet_nowdate.setText(dateStr + "(" + dayOfWeek + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
        for(InnerFragment f : pages){
//            f.setNewCafeteriaRecyclerAdapter(this.getContext());
        }
    }
}

