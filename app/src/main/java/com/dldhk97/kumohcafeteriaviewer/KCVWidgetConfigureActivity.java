package com.dldhk97.kumohcafeteriaviewer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.dldhk97.kumohcafeteriaviewer.enums.CafeteriaType;
import com.dldhk97.kumohcafeteriaviewer.enums.MealTimeType;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link KCVWidget KCVWidget} AppWidget.
 */
public class KCVWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.dldhk97.kumohcafeteriaviewer.KCVWidget";
    private static final String PREF_PREFIX_KEY = "KCVWidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Spinner widget_spinner_cafeteria;
    private Switch widget_switch;
    private SeekBar widget_seekbar;
    private TextView widget_textView_indicator;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = KCVWidgetConfigureActivity.this;

            // SharedPreference 저장을 위해 각 객체에서 정보 빼낸다.
            CafeteriaType cafeteriaType = CafeteriaType.getByIndex(widget_spinner_cafeteria.getSelectedItemPosition());
            boolean isBlack = widget_switch.isChecked();
            int transparent = 100 - widget_seekbar.getProgress();


            // 사용자가 입력한 정보를 로컬 저장한다.
            savePref(context, mAppWidgetId, cafeteriaType, isBlack, transparent);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            KCVWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public KCVWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void savePref(Context context, int appWidgetId, CafeteriaType cafeteriaType, boolean isBlack, int transparent) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();

        String cafeType = cafeteriaType != null ? cafeteriaType.toString() : CafeteriaType.getByIndex(0).toString();
        String mealTimeType = MealTimeType.BREAKFAST.toString();

        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "cafeteriaType", cafeType);   // 식당 유형 저장
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "isBlack", String.valueOf(isBlack));          // 흑백테마 저장
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "transparent", String.valueOf(transparent));  // 투명도 저장
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "mealTimeType", mealTimeType);                // 식사시간 설정
        prefs.apply();

        Log.d("aaaaa", "appWidgetId : " + String.valueOf(appWidgetId) + ", mealTimeType : " + mealTimeType + " saved on configActivity");
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static ArrayList<String> loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        final String cafeteriaType = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "cafeteriaType", "학생식당");
        final String isBlack = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "isBlack", "true");
        final String transparent = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "transparent", "50");
        final String mealTimeType = prefs.getString(PREF_PREFIX_KEY + appWidgetId + "mealTimeType", "조식");

        ArrayList<String> loadedPrefs = new ArrayList<String>() {{
            add(cafeteriaType);
            add(isBlack);
            add(transparent);
            add(mealTimeType);
        }};

        Log.d("aaaaa", "appWidgetId : " + String.valueOf(appWidgetId) + ", mealTimeType : " + mealTimeType + " loaded on configActivity");

        return loadedPrefs;
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.k_c_v_widget_configure);

        // 각 객체들 findViewById로 저장
        widget_spinner_cafeteria = findViewById(R.id.widget_spinner_cafeteria);         // 스피너
        widget_switch = findViewById(R.id.widget_switch);                               // 스위치
        widget_seekbar = findViewById(R.id.widget_seekbar);                             // 싞바
        widget_textView_indicator = findViewById(R.id.widget_textView_indicator);       // 싞바 퍼센트 표시

        findViewById(R.id.widget_button_confirm).setOnClickListener(mOnClickListener);  // 확정 버튼은 리스너 등록
        widget_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {   // 싞바 이벤트
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                widget_textView_indicator.setText(i + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

//        mAppWidgetText.setText(loadTitlePref(KCVWidgetConfigureActivity.this, mAppWidgetId));
    }
}

