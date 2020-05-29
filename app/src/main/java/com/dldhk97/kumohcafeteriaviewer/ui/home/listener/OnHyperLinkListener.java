package com.dldhk97.kumohcafeteriaviewer.ui.home.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.dldhk97.kumohcafeteriaviewer.UIHandler;

public class OnHyperLinkListener implements View.OnClickListener {
    String targetUrl = "about:blank";
    Context context;

    public OnHyperLinkListener(Context context, String targetUrl){
        this.context = context;
        this.targetUrl = targetUrl;
    }

    @Override
    public void onClick(View view) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
            context.startActivity(intent);
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[OnHyperLinkListener.onClick]\n" + e.getMessage());
        }
    }
}
