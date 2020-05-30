package com.dldhk97.kumohcafeteriaviewer.ui.favorite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.UIHandler;
import com.dldhk97.kumohcafeteriaviewer.data.FavoriteManager;
import com.dldhk97.kumohcafeteriaviewer.enums.ItemType;
import com.dldhk97.kumohcafeteriaviewer.model.Item;
import com.dldhk97.kumohcafeteriaviewer.ui.favorite.recyclerView.FavoriteRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FavoriteFragment extends Fragment implements View.OnClickListener{

    private FavoriteRecyclerAdapter favoriteRecyclerAdapter;
    private RecyclerView favoriteRecyclerView;
    private ViewGroup container;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);
        try{
            this.container = container;

            // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
            favoriteRecyclerView = root.findViewById(R.id.favorite_recyclerView);
            favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

            favoriteRecyclerAdapter = new FavoriteRecyclerAdapter(container.getContext());
            favoriteRecyclerView.setAdapter(favoriteRecyclerAdapter);

            // fab 설정
            FloatingActionButton favorite_fab = root.findViewById(R.id.favorite_fab);
            favorite_fab.setOnClickListener(this);

            // 뒤로가기 눌렀을 때 반응 없음
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {

                }
            };
            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }

        return root;
    }

    private final int FAVORITE_POPUP_REQUEST_CODE = 1;
    private final int FAVORITE_POPUP_RESULT_CODE = 2;

    @Override
    public void onClick(View view) {
        try{
            Intent intent = new Intent(container.getContext(), PopupActivity.class);
            Activity activity = (Activity)container.getContext();
            activity.startActivityForResult(intent, FAVORITE_POPUP_REQUEST_CODE);
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == FAVORITE_POPUP_REQUEST_CODE) {
                if (resultCode == FAVORITE_POPUP_RESULT_CODE) {
                    String itemName = data.getStringExtra("ItemName");
                    boolean isSucceed = FavoriteManager.getInstance().addItem(new Item(itemName, ItemType.FOOD));
                    String toastMsg = isSucceed ? itemName + " 이(가) 찜 등록되었습니다." : itemName + " 을(를) 찜 등록하는데 실패했습니다.";
                    UIHandler.getInstance().showToast(toastMsg);
                    favoriteRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
        catch (Exception e){
            UIHandler.getInstance().showToast(e.getMessage());
            e.printStackTrace();
        }
    }
}
