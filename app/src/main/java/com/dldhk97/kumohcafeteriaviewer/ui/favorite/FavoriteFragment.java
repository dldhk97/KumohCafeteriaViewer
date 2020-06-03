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
                    getActivity().finish();         // 뒤로가기 누르면 꺼지게
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

    public void showHelp(){
        String msg =
            "찜목록에 음식을 추가하면 식단표에서 하트표시가 추가됩니다.\n\n" +
            "알림 기능과 연동하여 찜목록이 포함된 경우만 알릴 수도 있습니다.\n" +
            "해당 기능은 설정에서 \'찜 메뉴가 포함된 식단만 알림\' 기능을 활성화하면 작동합니다.\n\n" +
            "찜 추가는 식단표 화면에서 식단을 클릭한 후 음식을 롱 터치하여 추가할 수 있습니다.\n" +
            "또한 하단의 + 버튼으로 수동 추가도 가능합니다.";

        UIHandler.getInstance().showAlert("찜목록 도움말", msg);
    }
}
