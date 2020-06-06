package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.PopupActivity;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener.NotificationItemTitleWatcher;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener.OnCafeteriaSelectedListener;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView.listener.OnMealTimeSelectedListener;
import com.dldhk97.kumohcafeteriaviewer.utility.TimeUtility;

public class NotificationRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private NotificationRecyclerAdapter adapter;

    private EditText recycleritem_notification_title;
    private Switch recycleritem_notification_switch;
    private ImageButton recycleritem_notification_delete;
    private Spinner recycleritem_notification_cafeteria;
    private Spinner recycleritem_notification_mealtime;
    private TextView recycleritem_notification_time;

    private NotificationItem currentItem;
    private Context context;

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
            this.context = itemView.getContext();
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[FavoriteRecyclerViewHolder.constructor]" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onBind(final NotificationItem notificationItem)throws Exception{
        try{
            this.currentItem = notificationItem;

            // 알람 이름
            recycleritem_notification_title.setText(notificationItem.getName());
            recycleritem_notification_title.addTextChangedListener(new NotificationItemTitleWatcher(notificationItem));

            // 식당 드롭다운, 선택된 식당 찾아 설정
            int cafeteriaPos = 0;
            String[] cafeteriaArr = CafeteriaType.getStringArray();
            for(String s : cafeteriaArr){
                if(notificationItem.getCafeteriaType().toString().equals(s)){
                    break;
                }
                cafeteriaPos++;
            }
            recycleritem_notification_cafeteria.setSelection(cafeteriaPos);
            recycleritem_notification_cafeteria.setOnItemSelectedListener(new OnCafeteriaSelectedListener(notificationItem));

            // 식사시간 드롭다운, 선택된 식당 찾아 설정
            int mealTimePos = 0;
            String[] mealTimeArr = MealTimeType.getStringArray();
            for(String s : mealTimeArr){
                if(notificationItem.getMealTimeType().toString().equals(s)){
                    break;
                }
                mealTimePos++;
            }
            recycleritem_notification_mealtime.setSelection(mealTimePos);
            recycleritem_notification_mealtime.setOnItemSelectedListener(new OnMealTimeSelectedListener(notificationItem));

            // 알람시간 리스너 설정
            int hour = notificationItem.getHour();
            int min = notificationItem.getMin();
            String timeStr = TimeUtility.getInstance().hourMinToAMPM(hour, min);
            recycleritem_notification_time.setText(timeStr);
            recycleritem_notification_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PopupActivity.class);
                    intent.putExtra("notificationItem", currentItem);
                    Activity activity = (Activity)context;
                    activity.startActivityForResult(intent, NOTIFICATION_POPUP_REQUEST_CODE);
                }
            });

            // 삭제버튼 리스너 설정
            recycleritem_notification_delete.setOnClickListener(this);

            // 스위치 리스너 설정
            recycleritem_notification_switch.setChecked(notificationItem.isActivated());
            recycleritem_notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // DB에 저장
                    notificationItem.setActivated(b);
                    NotificationItemManager.getInstance().updateItem(notificationItem);

                    // 알림 세팅
                    NotificationItemManager.getInstance().reloadNotification(context);

                    if(b){
                        UIHandler.getInstance().showToast(notificationItem.getName() + " 이(가) 설정되었습니다.");
                        toggleViewEnabled(false);
                    }
                    else{
                        toggleViewEnabled(true);
                    }
                }
            });

            // 활성화되있으면 뷰 잠금
            toggleViewEnabled(!notificationItem.isActivated());

        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }


    }

    private final int NOTIFICATION_POPUP_REQUEST_CODE = 3;

    // 알림 삭제버튼
    @Override
    public void onClick(View view) {
        boolean isSucceed = NotificationItemManager.getInstance().deleteItem(currentItem);
        if(!isSucceed){
            UIHandler.getInstance().showToast(currentItem.getName() + " 을(를) 삭제하는데 실패했습니다.");
        }

        adapter.notifyDataSetChanged();
    }

    @SuppressLint("ResourceAsColor")
    private void toggleViewEnabled(boolean isEnabled){
        recycleritem_notification_title.setEnabled(isEnabled);
        recycleritem_notification_cafeteria.setEnabled(isEnabled);
        recycleritem_notification_mealtime.setEnabled(isEnabled);
        recycleritem_notification_time.setEnabled(isEnabled);
        recycleritem_notification_delete.setEnabled(isEnabled);
        if(isEnabled){
            recycleritem_notification_delete.setImageResource(R.drawable.ic_delete);
            recycleritem_notification_time.setTextColor(Color.parseColor("#1abc9c"));
        }
        else{
            recycleritem_notification_delete.setImageResource(R.drawable.ic_delete_disabled);
            recycleritem_notification_time.setTextColor(Color.parseColor("#C9C9C9"));
        }
    }
}
