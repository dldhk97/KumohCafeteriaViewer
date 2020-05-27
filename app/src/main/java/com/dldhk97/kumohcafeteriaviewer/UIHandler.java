package com.dldhk97.kumohcafeteriaviewer;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class UIHandler {
    private static UIHandler _Instance;
    private MainActivity mainActivity;

    UIHandler(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        _Instance = this;
    }

    public static UIHandler getInstance(){
        return _Instance;
    }

    public void showToast(final String msg){
        try{
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
        catch (Exception e){
            Log.d("[UIHandler.showToast]", e.getMessage());
        }
    }

    public void showAlert(final String msg){
        try{
            final AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setMessage(msg);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alert.show();
                }
            });
        }
        catch (Exception e){
            Log.d("[UIHandler.showAlert]", e.getMessage());
        }
    }

    public void showAlert(final String title, final String msg){
        try{
            final AlertDialog.Builder alert = new AlertDialog.Builder(mainActivity);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setTitle(title);
            alert.setMessage(msg);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alert.show();
                }
            });
        }
        catch (Exception e){
            Log.d("[UIHandler.showAlert]", e.getMessage());
        }
    }
}
