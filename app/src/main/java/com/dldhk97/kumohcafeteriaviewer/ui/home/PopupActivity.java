package com.dldhk97.kumohcafeteriaviewer.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.ui.home.listener.OnHyperLinkListener;
import com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView.ItemRecyclerAdapter;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

public class PopupActivity extends Activity {

    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_popup);

            Intent intent = getIntent();
            menu = (Menu)intent.getSerializableExtra("menu");

            setupUiComponents();
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[Home.PopupActivity.onCreate]\n" + e.getMessage());
        }
    }

    private void setupUiComponents() throws Exception{
        // 제목 설정
        TextView popup_textView_title = findViewById(R.id.item_item_name);
        popup_textView_title.setText(menu.getCafeteriaType().toString());

        //날짜 설정
        TextView popup_textView_date = findViewById(R.id.popup_textView_date);
        String dateStr = DateUtility.dateToString(menu.getDate(), '.');
        String dayOfWeek = DateUtility.getDayOfWeek(menu.getDate());
        popup_textView_date.setText(dateStr + "(" + dayOfWeek + ")");

        // 식사시간(조식/중식/석식) 설정
        TextView popup_textView_mealTime = findViewById(R.id.popup_textView_mealTime);
        popup_textView_mealTime.setText(menu.getMealTimeType().toString());


        // 음식 리사이클러뷰 표시
        RecyclerView popup_recyclerView_items = findViewById(R.id.popup_recyclerView_items);
        popup_recyclerView_items.setLayoutManager(new LinearLayoutManager(this));

        ItemRecyclerAdapter itemRecyclerAdapter = new ItemRecyclerAdapter(this, menu.getItems());
        popup_recyclerView_items.setItemViewCacheSize(menu.getItems().size());
        popup_recyclerView_items.setAdapter(itemRecyclerAdapter);

        // 하이퍼링크 버튼 설정
        Button popup_button_link = findViewById(R.id.popup_button_link);
        String url = menu.getCafeteriaType().getURL();                          // url 설정
        url += "mode=menuList&srDt=" + dateStr; // 해당 날짜로 설정
        popup_button_link.setOnClickListener(new OnHyperLinkListener(this, url));
    }
}