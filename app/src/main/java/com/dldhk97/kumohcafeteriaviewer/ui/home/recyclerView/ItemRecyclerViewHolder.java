package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

public class ItemRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    private Item item;
    private ImageView item_item_favoriteIcon;
    private TextView item_item_name;

    public ItemRecyclerViewHolder(@NonNull View itemView, ItemRecyclerAdapter adapter) {
        super(itemView);

        try{
            this.item_item_favoriteIcon = itemView.findViewById(R.id.item_item_favorite);
            this.item_item_name = itemView.findViewById(R.id.item_item_name);


            // 클릭 리스너 설정
            itemView.setOnLongClickListener(this);
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.constructor]" + e.getMessage());
        }
    }

    public void onBind(Item item)throws Exception{
        this.item = item;

        // 찜된건지 체크해서 하트모양 표시
        if(item.getItemType() == ItemType.FOOD){
            boolean isFavorite = false;
            if(isFavorite){
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_favorite);
            }
            else{
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_notfavorite);
            }
        }

        // 아이템 명 설정
        item_item_name.setText(item.getItemName());

    }


    @Override
    public boolean onLongClick(View view) {
        if(item.getItemType() == ItemType.FOOD){
            boolean isFavorite = false;
            item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_favorite);
        }
        return false;
    }
}


