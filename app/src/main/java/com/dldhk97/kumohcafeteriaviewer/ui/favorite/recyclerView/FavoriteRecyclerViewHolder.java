package com.dldhk97.kumohcafeteriaviewer.ui.favorite.recyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

public class FavoriteRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView recycleritem_favorite_title;
    private ImageButton recycleritem_favorite_delete;
    private FavoriteRecyclerAdapter adapter;

    public FavoriteRecyclerViewHolder(@NonNull View itemView, FavoriteRecyclerAdapter adapter) {
        super(itemView);
        try{
            this.recycleritem_favorite_title = itemView.findViewById(R.id.recycleritem_favorite_title);
            this.recycleritem_favorite_delete = itemView.findViewById(R.id.recycleritem_favorite_delete);
            this.adapter = adapter;
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[FavoriteRecyclerViewHolder.constructor]" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onBind(Item item)throws Exception{
        try{
            // 음식명 표시
            recycleritem_favorite_title.setText(item.getItemName());
            // 삭제 버튼 리스너
            recycleritem_favorite_delete.setOnClickListener(this);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private final int resultCode = 1;

    @Override
    public void onClick(View view) {
        try{
            int pos = getLayoutPosition();
            if(pos != RecyclerView.NO_POSITION){
                Item i = FavoriteManager.getInstance().getCurrentItems().get(pos);
                FavoriteManager.getInstance().deleteItem(i);
                String toastMsg = i.getItemName() + "을(를) 찜 해제했습니다.";
                UIHandler.getInstance().showToast(toastMsg);
                adapter.notifyDataSetChanged();
            }
        }
        catch(Exception e){
            UIHandler.getInstance().showAlert("[FavoriteRecyclerViewHolder.onClick]" + e.getMessage());
            e.printStackTrace();
        }
    }

}
