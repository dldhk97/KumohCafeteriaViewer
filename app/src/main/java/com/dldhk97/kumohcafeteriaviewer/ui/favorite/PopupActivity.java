package com.dldhk97.kumohcafeteriaviewer.ui.favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;

public class PopupActivity extends Activity {
    private final int POPUP_RESULT_CODE_CONFIRMED = 2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_favorite_popup);
            setupUiComponents();
        }
        catch (Exception e){
            UIHandler.getInstance().showAlert("[Favorite.PopupActivity.onCreate]\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupUiComponents() throws Exception{
        // 제목 설정
        final EditText favorite_popup_editText = findViewById(R.id.favorite_popup_editText);

        // 버튼 설정
        Button favorite_popup_confirm = findViewById(R.id.favorite_popup_confirm);
        favorite_popup_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = favorite_popup_editText.getText().toString();
                if(text == null || text.isEmpty()){
                    UIHandler.getInstance().showToast("입력값이 비어있습니다.");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("ItemName", text);
                setResult(POPUP_RESULT_CODE_CONFIRMED, intent);
                finish();
            }
        });

        Button favorite_popup_cancel = findViewById(R.id.favorite_popup_cancel);
        favorite_popup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
