package com.dldhk97.kumohcafeteriaviewer.ui.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.dldhk97.kumohcafeteriaviewer.utility.ResourceUtility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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

            String name = "내 식사 알림 " + (nameOffset + 1);
            CafeteriaType cafeteriaType = CafeteriaType.stringTo(ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType)[0]);
            MealTimeType mealTimeType = MealTimeType.stringTo(ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType)[0]);
            int hour = 7;
            int min = 30;
            boolean activated = false;

            NotificationItem newItem = new NotificationItem(name, cafeteriaType, mealTimeType, hour, min, activated);
            NotificationItemManager.getInstance().addItem(newItem);
            notificationRecyclerAdapter.notifyDataSetChanged();
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
}
