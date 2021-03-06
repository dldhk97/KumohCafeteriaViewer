package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

public class ItemRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private Item item;
    private ConstraintLayout item_constraintlayout;
    private ImageView item_item_favoriteIcon;
    private TextView item_item_name;
    private ItemRecyclerAdapter adapter;
    private boolean isFavorite;

    public ItemRecyclerViewHolder(@NonNull View itemView, ItemRecyclerAdapter adapter) {
        super(itemView);

        try{
            this.item_constraintlayout = itemView.findViewById(R.id.item_constraintlayout);
            this.item_item_favoriteIcon = itemView.findViewById(R.id.item_item_favorite);
            this.item_item_name = itemView.findViewById(R.id.item_item_name);
            this.adapter = adapter;


            // 클릭 리스너 설정
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.constructor]" + e.getMessage());
        }
    }

    public void onBind(Item item)throws Exception{
        this.item = item;

        // 찜된건지 체크해서 하트모양 표시
        if(item.getItemType() == ItemType.FOOD){
            item_item_favoriteIcon.setVisibility(View.VISIBLE);
            isFavorite = FavoriteManager.getInstance().findItem(item.getItemName()) != null ? true : false;
            if(isFavorite){
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_favorite);
            }
            else{
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_notfavorite);
            }
        }
        else{
            item_item_favoriteIcon.setVisibility(View.GONE);
        }

        // 아이템 명 설정
        item_item_name.setText(item.getItemName());

    }


    @Override
    public boolean onLongClick(View view) {
        if(item.getItemType() == ItemType.FOOD){
            String toastMsg = null;
            if(isFavorite){
                FavoriteManager.getInstance().deleteItem(item);
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_notfavorite);
                toastMsg = item.getItemName() + " 을(를) 찜 해제했습니다.";
            }
            else{
                FavoriteManager.getInstance().addItem(item);
                item_item_favoriteIcon.setImageResource(R.drawable.ic_activity_popup_favorite);
                toastMsg = item.getItemName() + " 을(를) 찜하였습니다.";
            }
            // 찜에 추가
            UIHandler.getInstance().showToast(toastMsg);
            adapter.notifyDataSetChanged();

        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(item.getItemType() == ItemType.FOOD){
            UIHandler.getInstance().showToast("길게 눌러 찜 등록/해제 할 수 있습니다.");
        }

    }
}


