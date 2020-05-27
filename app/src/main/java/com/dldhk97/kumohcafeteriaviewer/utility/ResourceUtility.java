package com.dldhk97.kumohcafeteriaviewer.utility;

import android.content.Context;
import android.util.Log;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;

public class ResourceUtility {
    private static Context _context;
    private static ResourceUtility _instance;

    public void setContext(Context context){
        this._context = context;
    }

    public static ResourceUtility getInstance(){
        if(_instance == null){
            _instance = new ResourceUtility();
        }
        return _instance;
    }

    public String getUrlFromCafeteriaType(CafeteriaType cafeteriaType){
        if(_context == null){
            Log.d("","Please initialize context before use.");
            return null;
        }
        switch(cafeteriaType){
            case STUDENT:
                return _context.getResources().getStringArray(R.array.urls)[0];
            case STAFF:
                return _context.getResources().getStringArray(R.array.urls)[1];
            case SNACKBAR:
                return _context.getResources().getStringArray(R.array.urls)[2];
            case PUROOM:
                return _context.getResources().getStringArray(R.array.urls)[3];
            case OREUM1:
                return _context.getResources().getStringArray(R.array.urls)[4];
            case OREUM3:
                return _context.getResources().getStringArray(R.array.urls)[5];
            default:
                break;
        }
        return null;
    }
}
