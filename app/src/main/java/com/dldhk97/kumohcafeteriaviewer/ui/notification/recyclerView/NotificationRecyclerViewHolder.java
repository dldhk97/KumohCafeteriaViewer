package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;
import com.dldhk97.kumohcafeteriaviewer.utility.ResourceUtility;

public class NotificationRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private NotificationRecyclerAdapter adapter;

    private EditText recycleritem_notification_title;
    private Switch recycleritem_notification_switch;
    private ImageButton recycleritem_notification_delete;
    private Spinner recycleritem_notification_cafeteria;
    private Spinner recycleritem_notification_mealtime;
    private EditText recycleritem_notification_time;

    private NotificationItem currentItem;

    public NotificationRecyclerViewHolder(@NonNull View itemView, NotificationRecyclerAdapter adapter) {
        super(itemView);
        try{
            this.recycleritem_notification_title = itemView.findViewById(R.id.recycleritem_notification_title);
            this.recycleritem_notification_switch = itemView.findViewById(R.id.recycleritem_notification_switch);
            this.recycleritem_notification_delete = itemView.findViewById(R.id.recycleritem_notification_delete);
            this.recycleritem_notification_cafeteria = itemView.findViewById(R.id.recycleritem_notification_cafeteria);
            this.recycleritem_notification_mealtime = itemView.findViewById(R.id.recycleritem_notification_mealtime);
            this.recycleritem_notification_time = itemView.findViewById(R.id.recycleritem_notification_time);
            this.adapter = adapter;
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[FavoriteRecyclerViewHolder.constructor]" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onBind(NotificationItem notificationItem)throws Exception{
        this.currentItem = notificationItem;

        // 알람 이름
        recycleritem_notification_title.setText(notificationItem.getName());

        // 스위치
        recycleritem_notification_switch.setChecked(notificationItem.isActivated());

        // 식당 셀렉션
        int cafeteriaPos = 0;
        String[] cafeteriaArr = ResourceUtility.getInstance().getResources().getStringArray(R.array.cafeteriaType);
        for(String s : cafeteriaArr){
            if(notificationItem.getCafeteriaType().toString().equals(s)){
                break;
            }
            cafeteriaPos++;
        }
        recycleritem_notification_cafeteria.setSelection(cafeteriaPos);

        // 식사시간
        int mealTimePos = 0;
        String[] mealTimeArr = ResourceUtility.getInstance().getResources().getStringArray(R.array.mealTimeType);
        for(String s : mealTimeArr){
            if(notificationItem.getMealTimeType().toString().equals(s)){
                break;
            }
            mealTimePos++;
        }
        recycleritem_notification_mealtime.setSelection(mealTimePos);

        // 알람시간
        int hour = notificationItem.getHour();
        int min = notificationItem.getMin();
        String timeStr = hour + ":" + min;
        recycleritem_notification_time.setText(timeStr);

        // 삭제버튼
        recycleritem_notification_delete.setOnClickListener(this);

    }


    // 알림 삭제버튼
    @Override
    public void onClick(View view) {
        boolean isSucceed = NotificationItemManager.getInstance().deleteItem(currentItem);
        String toastMsg = "";
        if(isSucceed){
            toastMsg = currentItem.getName() + " 이(가) 삭제되었습니다.";
        }
        else{
            toastMsg = currentItem.getName() + " 을(를) 삭제하는데 실패했습니다.";
        }
        UIHandler.getInstance().showToast(toastMsg);
        adapter.notifyDataSetChanged();
    }
}
