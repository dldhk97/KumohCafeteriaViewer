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
            UIHandler.getInstance().showAlert("[FavoriteRecyclerAdapter.onCreateViewHolder]\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteRecyclerViewHolder holder, int position) {
        try{
            if(FavoriteManager.getInstance().getCurrentItems() != null)
                holder.onBind(FavoriteManager.getInstance().getCurrentItems().get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[FavoriteRecyclerAdapter.onBindViewHolder]\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        try{
            if(FavoriteManager.getInstance().getCurrentItems() != null){
                return FavoriteManager.getInstance().getCurrentItems().size();
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}
