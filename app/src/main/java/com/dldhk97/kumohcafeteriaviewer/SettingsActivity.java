package com.dldhk97.kumohcafeteriaviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.dldhk97.kumohcafeteriaviewer.data.DatabaseManager;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.data.MenuManager;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initPrefs(this);

    }

    // 뒤로가기 버튼 터치 시 홈으로 돌아가게 함.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // 식단 초기화 버튼 설정
            Preference menus_data_reset_key = getPreferenceManager().findPreference(getContext().getResources().getString(R.string.menus_data_reset_key));
            if (menus_data_reset_key != null) {
                menus_data_reset_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("식단 데이터를 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        resetMenusData();
                                        UIHandler.getInstance().showToast("식단 데이터가 초기화되었습니다.");
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){

                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }

            // 모든 데이터 초기화 버튼 설정
            Preference all_data_reset_key = getPreferenceManager().findPreference(getContext().getResources().getString(R.string.all_data_reset_key));
            if (all_data_reset_key != null) {
                all_data_reset_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("모든 데이터를 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        resetAllData();
                                        UIHandler.getInstance().showToast("모든 데이터가 초기화되었습니다.");
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){

                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }
        }

        private void resetMenusData(){
            DatabaseManager.getInstance().dropMenusTable();
            DatabaseManager.getInstance().createTable();
            MenuManager.getInstance().sync();
        }

        private void resetAllData(){
            DatabaseManager.getInstance().dropTables();
            DatabaseManager.getInstance().createTable();

            MenuManager.getInstance().sync();
            NotificationItemManager.getInstance().sync();
            FavoriteManager.getInstance().sync();
        }
    }

    private void initPrefs(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean ring = prefs.getBoolean("ring", false);
        boolean vibrate = prefs.getBoolean("vibrate", false);
        boolean wakeup = prefs.getBoolean("wakeup", false);
        boolean favorite_only = prefs.getBoolean("favorite_only", false);
        boolean no_notify_holiday = prefs.getBoolean("no_notify_holiday", false);
        String default_cafeteria = prefs.getString("default_cafeteria", "알수없음");
    }
}