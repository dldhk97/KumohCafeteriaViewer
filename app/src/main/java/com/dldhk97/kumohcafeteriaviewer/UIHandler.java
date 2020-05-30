package com.dldhk97.kumohcafeteriaviewer;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.dldhk97.kumohcafeteriaviewer.enums.ExceptionType;
import com.dldhk97.kumohcafeteriaviewer.model.MyException;

public class UIHandler {
    private static UIHandler _instance;
    private MainActivity mainActivity;

    public static UIHandler getInstance(){
        if(_instance == null)
            _instance = new UIHandler();
        return _instance;
    }

    public void setMainActivity(final MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void showToast(final String msg){
        try{
            if(mainActivity == null){
                throw new MyException(ExceptionType.CONTEXT_NOT_INITIALIZED, "UIHandler not initialized");
            }



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
            if(mainActivity == null){
                throw new MyException(ExceptionType.CONTEXT_NOT_INITIALIZED, "UIHandler not initialized");
            }

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
            if(mainActivity == null){
                throw new MyException(ExceptionType.CONTEXT_NOT_INITIALIZED, "UIHandler not initialized");
            }

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
