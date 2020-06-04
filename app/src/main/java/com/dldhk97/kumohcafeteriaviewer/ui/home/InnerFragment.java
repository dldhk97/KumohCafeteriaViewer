package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;
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
    private HomeFragment parent;
    private SwipeRefreshLayout menu_inner_swipeRefresh;

    private boolean isBusy = false;

    public InnerFragment(CafeteriaType cafeteriaType, HomeFragment parent){
        this.cafeteriaType = cafeteriaType;
        this.parent = parent;
    }

    public CafeteriaType getCafeteriaType() {
        return cafeteriaType;
    }

    public CafeteriaRecyclerAdapter getCafeteriaRecyclerAdapter(){return cafeteriaRecyclerAdapter;}

    public boolean isBusy() {
        return isBusy;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home_inner, container, false);

        updateMenus(currentDate, false);

        // 스와이프 리프레셔 등록
        menu_inner_swipeRefresh = root.findViewById(R.id.menu_inner_swipeRefresh);
        menu_inner_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    parent.updateCurrentDateView(true);     // 모든 페이지 리프레시
                }
                catch (Exception e){
                    e.printStackTrace();
                    UIHandler.getInstance().showToast(e.getMessage());
                }
            }
        });

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        menu_inner_recyclerView = root.findViewById(R.id.menu_inner_recyclerView);
        menu_inner_recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        cafeteriaRecyclerAdapter = new CafeteriaRecyclerAdapter(container.getContext(), currentMenus);
        menu_inner_recyclerView.setAdapter(cafeteriaRecyclerAdapter);

        // 생성되었는데 리프레시중인 이너프래그먼트가 있다면, 얘도 리프레시 상태로 바꿈. 그래서 리프레시 중복으로 일어나지 않게 함.
        // 리프레시중인 이너프레그먼트는 다끝나면 홈프래그먼트(parent)에게 알려주고, 모든 이너에게 stopRefreshing하게 알림.
        if(parent.isBusyFragExists()){
            menu_inner_swipeRefresh.setRefreshing(true);
        }

        return root;
    }


    // 날짜 변경 요청 시 호출됨. 리사이클러 뷰 내 항목 업데이트함.
    public void updateMenus(Calendar date, boolean isForceUpdate){
        UpdateTask backgroundTask = new UpdateTask();
        backgroundTask.setOnUpdateCompleteReceivedEvent(new UpdateCompleteListener());
        backgroundTask.execute(new Pair<Calendar, Boolean>(date, isForceUpdate));
        if(menu_inner_swipeRefresh != null)
            menu_inner_swipeRefresh.setRefreshing(true);
        isBusy = true;
    }

    public void stopRrefreshing(){
        if(menu_inner_swipeRefresh != null)
            menu_inner_swipeRefresh.setRefreshing(false);
    }

    // -----------------------------------------------------------//

    // Update 리스너. Update 끝나면 얘가 알려줌.
    private class UpdateCompleteListener {
        public void onUpdateComplete(boolean isSucceed) {
            if(cafeteriaRecyclerAdapter != null){
                cafeteriaRecyclerAdapter.notifyDataSetChanged();
            }
            isBusy = false;
            parent.requestStopRefreshing();
        }
    }

    // 데이터 가져오기 및 파싱 백그라운드에서 돌리게 함.
    private class UpdateTask extends AsyncTask<Pair<Calendar, Boolean>, Integer, Integer > {
        private UpdateCompleteListener updateCompleteListener;

        @Override
        protected Integer doInBackground(Pair<Calendar, Boolean>... pairs) {
            try {
                Calendar date = pairs[0].first;
                boolean isForceUpdate = pairs[0].second.booleanValue();
                // 날짜만 남긴다. 시간은 제외
                currentDate = DateUtility.remainOnlyDate(date);

                // 주어진 날짜의 식단이 존재하지 않으면 파싱
                weekMenus = MenuManager.getInstance().getWeekMenus(cafeteriaType, currentDate, isForceUpdate);

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
                    emptyMenu.addItem(new Item("식사정보 없음", ItemType.ETC));
                    currentMenus.add(emptyMenu);
                }

                if(cafeteriaRecyclerAdapter == null){
                    return null;
                }

                // 기숙사 식단 3칸으로 만들어놨을때 맨 아래에 '등록된 메뉴가 없습니다' 생기는거 임시 픽스.
                // 파싱할때 빈 칸이 있어서 '등록된 메뉴가 없습니다' 생기고, sync 할 때 DB에 넣었다 빼면서 병합됨.
                for(Menu m : currentMenus){
                    if(m.getItems().size() > 1){
                        for(Item i : m.getItems()){
                            if(i.getItemName().contains("등록된 메뉴가 없습니다.")){
                                m.removeItem(i);
                                break;
                            }
                        }
                    }
                }

                cafeteriaRecyclerAdapter.updateData(currentMenus);

            } catch (Exception e) {
                UIHandler.getInstance().showToast(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            updateCompleteListener.onUpdateComplete(true);
        }

        // 데이터 전달을 위한 리스너 설정
        public void setOnUpdateCompleteReceivedEvent(UpdateCompleteListener listener){
            updateCompleteListener = listener;
        }
    }

    // --------------------------------//

    // 이 프래그먼트 안에 주어진 날짜의 식단이 존재하는지 체크
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
