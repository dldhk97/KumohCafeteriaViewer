package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;

import java.util.ArrayList;

public class CafeteriaRecyclerAdapter extends RecyclerView.Adapter<CafeteriaRecyclerViewHolder> {
    private LayoutInflater inflater;
    private final Context context;
    private ArrayList<Menu> menus;

    public CafeteriaRecyclerAdapter(final Context context, ArrayList<Menu> menus){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.menus = menus;
    }

    public Context getContext(){
        return context;
    }

    @NonNull
    @Override
    public CafeteriaRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
            View itemView = inflater.inflate(R.layout.recycleritem_menu, parent, false);
            return new CafeteriaRecyclerViewHolder(itemView, this);
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[CafeteriaRecyclerViewHolder.onCreateViewHolder]\n" + e.getMessage());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CafeteriaRecyclerViewHolder holder, int position) {
        try{
            if(menus != null)
                holder.onBind(menus.get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[Adapter.onBindViewHolder]\n" + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        if(menus != null){
            return menus.size();
        }
        return 0;
    }

    public void updateData(final ArrayList<Menu> menus){
        this.menus = menus;
    }
}
