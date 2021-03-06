package com.dldhk97.kumohcafeteriaviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dldhk97.kumohcafeteriaviewer.data.DatabaseManager;
import com.dldhk97.kumohcafeteriaviewer.data.NotificationItemManager;
import com.dldhk97.kumohcafeteriaviewer.enums.NetworkStatusType;
import com.dldhk97.kumohcafeteriaviewer.ui.favorite.FavoriteFragment;
import com.dldhk97.kumohcafeteriaviewer.ui.home.HomeFragment;
import com.dldhk97.kumohcafeteriaviewer.ui.notification.NotificationFragment;
import com.dldhk97.kumohcafeteriaviewer.utility.NetworkStatus;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_notification, R.id.nav_favorite)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        try{
            // UI핸들러 셋업
            UIHandler uh = UIHandler.getInstance();
            uh.setMainActivity(this);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

        try{
            // 데이터베이스 매니저 로드
            DatabaseManager dm = DatabaseManager.getInstance();
            dm.setContextIfNotExist(this);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }


        try{
            // 네트워크 체크
            if(NetworkStatus.checkStatus(this) != NetworkStatusType.CONNECTED){
                UIHandler.getInstance().showToast("금오공과대학교 홈페이지 연결에 실패했습니다!");
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

    }

    // 메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // 메뉴 클릭 시 동작
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_help:
                Fragment navHostFragment = getSupportFragmentManager().getFragments().get(0);
                for(Fragment f : navHostFragment.getChildFragmentManager().getFragments()){
                    if(f.isVisible()){
                        if(f.getClass() == HomeFragment.class){
                            HomeFragment hf = (HomeFragment)f;
                            hf.showHelp();
                        }
                        else if(f.getClass() == FavoriteFragment.class){
                            FavoriteFragment ff = (FavoriteFragment)f;
                            ff.showHelp();
                        }
                        else if(f.getClass() == NotificationFragment.class){
                            NotificationFragment nf = (NotificationFragment)f;
                            nf.showHelp();
                        }
                        break;
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            // Navigation 구조에서 onActivityResult를 받기 위한 것. Fragment 에서는 onActivityResult가 안되서 Activity에서 받아 호출해야댐.
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().getFragments().get(0);
            for(Fragment f : navHostFragment.getChildFragmentManager().getFragments()){
                f.onActivityResult(requestCode, resultCode, data);      // 프래그먼트 모두에게 알려주는데, 어짜피 받는놈은 Code로 자기껀지 아닌지 분별함.
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

    }
}
