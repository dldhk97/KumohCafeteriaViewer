package com.dldhk97.kumohcafeteriaviewer.utility;

import android.content.Context;
import android.content.res.Resources;
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



    public Resources getResources(){
        if(_context == null){
            Log.d("","Please initialize context before use.");
            return null;
        }
        return _context.getResources();
    }
}
