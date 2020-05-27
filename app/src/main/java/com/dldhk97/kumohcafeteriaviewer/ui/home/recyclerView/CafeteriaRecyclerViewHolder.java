package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.MainActivity;
import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.utility.DateUtility;

public class CafeteriaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView imageView_icon;
    private TextView textView_date;
    private TextView textView_mealTime;
    private TextView textView_menus;

    private Menu menu;

    private Context context;

    public CafeteriaRecyclerViewHolder(@NonNull View itemView, CafeteriaRecyclerAdapter adapter) {
        super(itemView);
        context = adapter.getContext();
        try{
            this.imageView_icon = itemView.findViewById(R.id.imageView_icon);
            this.textView_date = itemView.findViewById(R.id.popup_textView_title);
            this.textView_mealTime = itemView.findViewById(R.id.textView_mealTime);
            this.textView_menus = itemView.findViewById(R.id.textView_menus);

            // 클릭 리스너 설정
            itemView.setOnClickListener(this);
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.constructor]" + e.getMessage());
        }
    }

    public void onBind(Menu menu)throws Exception{
        this.menu = menu;
        // 아이콘 설정
//        int imageId = menu.getImageId();
//        if(imageId == 0){
//            imageId = R.drawable.unknown;
//        }
//        imageView_icon.setImageResource(imageId);

        // 날짜 설정
        String dateStr = DateUtility.DateToString(menu.getDate(), '.');
        textView_date.setText(dateStr);

        // 식사시간 설정
        textView_mealTime.setText(menu.getMealTimeType().toString());

        // 음식 간단히 표시
        StringBuilder foodsStr = new StringBuilder();
        for(Item item : menu.getItems()){
            if(item.getItemName().contains("[") || item.getItemName().contains("*") || item.getItemName().contains("-")
                    || item.getItemName().contains("]") || item.getItemName().contains("식당 안에서 식사는")
                    || item.getItemName().contains("금지합니다") || item.getItemName().contains("~")){           // 진짜 음식만 남긴다.
                continue;
            }
            foodsStr.append(item.getItemName() + " ");
            if(foodsStr.length() > 20){
                foodsStr.append("...");
                break;
            }
        }
        textView_menus.setText(foodsStr.toString());

    }

    private final int resultCode = 1;

    @Override
    public void onClick(View view) {
        try{
            int pos = getLayoutPosition();
            if(pos != RecyclerView.NO_POSITION){
//            UIHandler.getInstance().showAlert(menu.toString());
//                Intent intent = new Intent(context, PopupActivity.class);
//                intent.putExtra("menu", menu);
//                Activity activity = (Activity)context;
//                activity.startActivityForResult(intent, resultCode);
            }
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.onClick]" + e.getMessage());
        }
    }
}
