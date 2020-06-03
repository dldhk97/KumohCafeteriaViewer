package com.dldhk97.kumohcafeteriaviewer.ui.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.NotificationRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationFragment extends Fragment implements View.OnClickListener {

    private NotificationRecyclerAdapter notificationRecyclerAdapter;
    private RecyclerView notificationRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification, container, false);

        try{
            // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
            notificationRecyclerView = root.findViewById(R.id.notification_recyclerView);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

            notificationRecyclerAdapter = new NotificationRecyclerAdapter(container.getContext());
            notificationRecyclerView.setAdapter(notificationRecyclerAdapter);

            // fab 설정
            FloatingActionButton favorite_fab = root.findViewById(R.id.notification_fav);
            favorite_fab.setOnClickListener(this);

            // 뒤로가기 눌렀을 때 홈으로 가지 않고 꺼지게 하자.
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    getActivity().finish();
                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

        return root;
    }

    // 알림 추가
    @Override
    public void onClick(View view) {
        try{
            // 임시 알림 생성을 위한 알림 개수 체크
            ArrayList<NotificationItem> arr = NotificationItemManager.getInstance().getCurrentItems();
            int nameOffset = arr != null ? arr.size() : 0;

            // 알람 정보를 생성함.
            String name = "내 식사 알림 " + (nameOffset + 1);
            CafeteriaType cafeteriaType = CafeteriaType.STUDENT;
            MealTimeType mealTimeType = MealTimeType.BREAKFAST;
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR);
            int min = now.get(Calendar.MINUTE);
            boolean activated = false;

            // 알람 객체 생성해서 추가
            NotificationItem newItem = new NotificationItem(name, cafeteriaType, mealTimeType, hour, min, activated);
            NotificationItemManager.getInstance().addItem(newItem);
            notificationRecyclerAdapter.notifyDataSetChanged();

            // 최하단으로 스크롤
            int currentItemSize = NotificationItemManager.getInstance().getCurrentItems().size();
            if(currentItemSize > 0){
                notificationRecyclerView.smoothScrollToPosition(currentItemSize - 1);
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private final int NOTIFICATION_POPUP_REQUEST_CODE = 3;
    private final int NOTIFICATION_POPUP_RESULT_CODE = 4;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == NOTIFICATION_POPUP_REQUEST_CODE) {
                if (resultCode == NOTIFICATION_POPUP_RESULT_CODE) {
                    NotificationItem notificationItem = (NotificationItem)data.getSerializableExtra("notificationItem");

                    boolean isSucceed = NotificationItemManager.getInstance().updateItem(notificationItem);
                    notificationRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    public void showHelp(){
        String msg =
            "식사 알림을 등록하면 원하는 시간에 식단 알림을 받아볼 수 있습니다.\n\n" +
            "알림의 추가는 하단의 + 버튼을 통해 수 있습니다.\n" +
            "알림의 삭제는 알림을 먼저 해제한 후 휴지통 버튼을 터치하면 됩니다.\n\n" +
            "우측 상단 설정에서 소리, 진동, 찜 메뉴가 포함된 식단만 알림, " +
            "주말에는 알리지 않음과 같은 추가적인 설정을 할 수 있습니다.";


        UIHandler.getInstance().showAlert("식사 알림 도움말", msg);
    }
}
