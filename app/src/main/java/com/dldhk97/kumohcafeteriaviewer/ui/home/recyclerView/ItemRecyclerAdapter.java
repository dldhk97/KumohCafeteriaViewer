package com.dldhk97.kumohcafeteriaviewer.ui.home.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.model.Menu;

import java.util.ArrayList;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerViewHolder> {
    private LayoutInflater inflater;
    private final Context context;
    private ArrayList<Item> items;

    public ItemRecyclerAdapter(final Context context, ArrayList<Item> items){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ItemRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
            View itemView = inflater.inflate(R.layout.item_item, parent, false);
            return new ItemRecyclerViewHolder(itemView, this);
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[ItemRecyclerViewHolder.onCreateViewHolder]\n" + e.getMessage());
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRecyclerViewHolder holder, int position) {
        try{
            if(items != null)
                holder.onBind(items.get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[Adapter.onBindViewHolder]\n" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        if(items != null){
            return items.size();
        }
        return 0;
    }
}
