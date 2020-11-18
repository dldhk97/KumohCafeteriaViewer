package com.dldhk97.kumohcafeteriaviewer.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class NetworkStatus {

    private static NetworkStatusType currentStatus;

    public static NetworkStatusType getCurrentStatus() {
        return currentStatus;
    }

    public static NetworkStatusType checkStatus(final Context context) throws Exception{
        int sdk = Build.VERSION.SDK_INT;
        if(sdk >= Build.VERSION_CODES.M){
            currentStatus = getConnectivityStatus(context);
        }
        else{
            currentStatus = getConnectivityStatusOld(context);
        }
        return currentStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static NetworkStatusType getConnectivityStatus(final Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());

        if(nc != null){
            if(tryConnectToKIT())
                return NetworkStatusType.CONNECTED;
        }
        return NetworkStatusType.DISCONNECTED;
    }

    private static NetworkStatusType getConnectivityStatusOld(final Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            if(tryConnectToKIT())
                return NetworkStatusType.CONNECTED;
        }
        return NetworkStatusType.DISCONNECTED;
    }

    private static boolean tryConnectToKIT(){
        try{
            NetworkCheckTask nct = new NetworkCheckTask();
            Boolean result = (Boolean) nct.execute("").get();
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private static String TEST_URL = "https://kumoh.ac.kr/ko/restaurant01.do?";
    private static int TIMEOUT = 10000;

    private static class NetworkCheckTask extends AsyncTask {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            SSLConnect ssl = new SSLConnect();
            ssl.postHttps(TEST_URL,  TIMEOUT, TIMEOUT);
            final Connection connection = Jsoup.connect(TEST_URL);
            connection.timeout(TIMEOUT);

            // n번까지 파싱 시도해보고 안되면 실패
            int tryCnt = 2;
            while(tryCnt-- > 0){
                try{
                    Document doc = connection.get();
                    if(doc != null)
                        return true;
                }
                catch (Exception e){
                    Log.d("aaaaa","connection.get()\n" + e.getMessage());
                    if(tryCnt <= 0){
                        return false;
                    }
                }
            }
            return false;
        }
    }
}
