package com.dldhk97.kumohcafeteriaviewer.ui.favorite.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.model.Item;

import java.util.ArrayList;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewHolder> {
    private LayoutInflater inflater;
    private final Context context;

    public FavoriteRecyclerAdapter(final Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

    @NonNull
    @Override
    public FavoriteRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
            View itemView = inflater.inflate(R.layout.recycleritem_favorite, parent, false);
            return new FavoriteRecyclerViewHolder(itemView, this);
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.onCreateViewHolder]\n" + e.getMessage());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteRecyclerViewHolder holder, int position) {
        try{
            if(FavoriteManager.getInstance().getCurrentFavorites() != null)
                holder.onBind(FavoriteManager.getInstance().getCurrentFavorites().get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[Adapter.onBindViewHolder]\n" + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        if(FavoriteManager.getInstance().getCurrentFavorites() != null){
            return FavoriteManager.getInstance().getCurrentFavorites().size();
        }
        return 0;
    }
}
