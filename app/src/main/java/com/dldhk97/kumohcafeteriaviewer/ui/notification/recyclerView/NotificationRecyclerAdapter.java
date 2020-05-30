package com.dldhk97.kumohcafeteriaviewer.ui.notification.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerViewHolder> {
    private LayoutInflater inflater;
    private final Context context;

    public NotificationRecyclerAdapter(final Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public Context getContext(){
        return context;
    }

    @NonNull
    @Override
    public NotificationRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try{
            View itemView = inflater.inflate(R.layout.recycleritem_notification, parent, false);
            return new NotificationRecyclerViewHolder(itemView, this);
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[NotificationRecyclerAdapter.onCreateViewHolder]\n" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationRecyclerViewHolder holder, int position) {
        try{
            if(NotificationItemManager.getInstance().getCurrentItems() != null)
                holder.onBind(NotificationItemManager.getInstance().getCurrentItems().get(position));

        }
        catch(Exception e){
            UIHandler.getInstance().showToast("[NotificationRecyclerAdapter.onBindViewHolder]\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        try{
            if(NotificationItemManager.getInstance().getCurrentItems() != null){
                return NotificationItemManager.getInstance().getCurrentItems().size();
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }
}
