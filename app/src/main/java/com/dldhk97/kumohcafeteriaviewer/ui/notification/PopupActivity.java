package com.dldhk97.kumohcafeteriaviewer.ui.notification;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.NotificationItem;

public class PopupActivity extends Activity {
    private final int NOTIFICATION_POPUP_RESULT_CODE = 4;
    private NotificationItem notificationItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_notification_popup);

            Intent intent = getIntent();
            notificationItem = (NotificationItem)intent.getSerializableExtra("notificationItem");

            setupUiComponents();
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[notification.PopupActivity.onCreate]\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupUiComponents() throws Exception{
        final TimePicker notification_popup_timepicker = findViewById(R.id.notification_popup_timepicker);

        int orgHour = notificationItem.getHour();
        int orgMin = notificationItem.getMin();

        if(Build.VERSION.SDK_INT < 23)
        {
            notification_popup_timepicker.setCurrentHour(orgHour);
            notification_popup_timepicker.setCurrentMinute(orgMin);
        }
        else{
            notification_popup_timepicker.setHour(orgHour);
            notification_popup_timepicker.setMinute(orgMin);
        }


        // 설정 버튼 설정
        Button notification_popup_confirm = findViewById(R.id.notification_popup_confirm);
        notification_popup_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour, min;
                if(Build.VERSION.SDK_INT < 23)
                {
                    hour = notification_popup_timepicker.getCurrentHour();
                    min = notification_popup_timepicker.getCurrentMinute();
                }
                else
                {
                    hour = notification_popup_timepicker.getHour();
                    min = notification_popup_timepicker.getMinute();
                }

                notificationItem.setHour(hour);
                notificationItem.setMin(hour);

                // 결과 반환
                Intent intent = new Intent();
                intent.putExtra("notificationItem", notificationItem);
                setResult(NOTIFICATION_POPUP_RESULT_CODE, intent);
                finish();
            }
        });

        // 취소 버튼 설정
        Button notification_popup_cancel = findViewById(R.id.notification_popup_cancel);
        notification_popup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
