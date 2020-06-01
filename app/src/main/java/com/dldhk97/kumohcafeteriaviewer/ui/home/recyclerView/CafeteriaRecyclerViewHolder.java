package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;
import com.dldhk97.kumohcafeteriaviewer.ui.home.PopupActivity;

public class CafeteriaRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView recycleritem_menu_title;
    private ImageView recycleritem_menu_background;
    private TextView recycleritem_menu_mealTime;
    private TextView recycleritem_menu_time;

    private Menu menu;

    private Context context;

    public CafeteriaRecyclerViewHolder(@NonNull View itemView, CafeteriaRecyclerAdapter adapter) {
        super(itemView);
        context = adapter.getContext();
        try{
            this.recycleritem_menu_title = itemView.findViewById(R.id.recycleritem_menu_title);
            this.recycleritem_menu_background = itemView.findViewById(R.id.recycleritem_menu_background);
            this.recycleritem_menu_mealTime = itemView.findViewById(R.id.recycleritem_menu_mealTime);
            this.recycleritem_menu_time =itemView.findViewById(R.id.recycleritem_menu_time);


            // 클릭 리스너 설정
            itemView.setOnClickListener(this);
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.constructor]" + e.getMessage());
        }
    }

    public void onBind(Menu menu)throws Exception{
        this.menu = menu;

        // 식사 가능 시간 설정
        String eatableMealTime = menu.getMealTimeType().getEatableTime();

        // 조식/중식/석식 표시
        recycleritem_menu_time.setText(eatableMealTime);

        // 카드뷰 배경 이미지 설정
        recycleritem_menu_background.setImageResource(getCardViewBackground(menu));

        // 식사시간 설정
        if(!menu.isOpen()){
            recycleritem_menu_mealTime.setText("식사정보 없음");
        }
        else{
            recycleritem_menu_mealTime.setText(menu.getMealTimeType().toString());
        }

        // 음식 간단히 표시
        StringBuilder foodsStr = new StringBuilder();
        int cnt = 0;
        for(Item item : menu.getItems()){
            Item isFavorite = FavoriteManager.getInstance().findItem(item.getItemName());
            String favoriteEmoji = isFavorite != null ? "♥ " : "";
            foodsStr.append(favoriteEmoji + item.getItemName() + "\n");
            if(cnt++ > 7){
                foodsStr.append("...");
                break;
            }

        }
        recycleritem_menu_title.setText(foodsStr.toString());

    }

    private final int resultCode = 1;

    @Override
    public void onClick(View view) {
        try{
            int pos = getLayoutPosition();
            if(pos != RecyclerView.NO_POSITION){
                Intent intent = new Intent(context, PopupActivity.class);
                intent.putExtra("menu", menu);
                Activity activity = (Activity)context;
                activity.startActivityForResult(intent, resultCode);
            }
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.onClick]" + e.getMessage());
        }
    }

    private int getCardViewBackground(Menu menu){
        if(!menu.isOpen()){
            return R.drawable.closed;
        }

        switch (menu.getMealTimeType()){
            case BREAKFAST:
                return R.drawable.breakfast;
            case LUNCH:
                return R.drawable.lunch2;
            case DINNER:
                return R.drawable.dinner;
            case ONECOURSE:
                return R.drawable.fastfood;
            case UNKNOWN:
                return R.drawable.closed;
            default:
                return R.drawable.closed;
        }
    }


}
