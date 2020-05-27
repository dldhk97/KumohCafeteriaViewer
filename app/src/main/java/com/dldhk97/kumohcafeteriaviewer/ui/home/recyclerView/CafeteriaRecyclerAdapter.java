package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.DayMenus;

import java.util.Calendar;
import java.util.HashMap;

public class CafeteriaRecyclerAdapter extends RecyclerView.Adapter<CafeteriaRecyclerViewHolder> {
    private LayoutInflater inflater;
    private final Context context;
    private DayMenus dayMenus;

    public CafeteriaRecyclerAdapter(final Context context, DayMenus dayMenus){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.dayMenus = dayMenus;
    }

    public Context getContext(){
        return context;
    }

    @NonNull
    @Override
    public CafeteriaRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
//            View itemView = inflater.inflate(R.layout.item_menu, parent, false);
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
            if(dayMenus != null)
                holder.onBind(dayMenus.getMenus().get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[Adapter.onBindViewHolder]\n" + e.getMessage());
        }

    }


    @Override
    public int getItemCount() {
        if(dayMenus != null)
            return dayMenus.getMenus().size();
        return 0;
    }
}
