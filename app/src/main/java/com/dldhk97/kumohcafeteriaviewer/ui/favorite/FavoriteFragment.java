package com.dldhk97.kumohcafeteriaviewer.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dldhk97.kumohcafeteriaviewer.R;
import com.dldhk97.kumohcafeteriaviewer.ui.favorite.recyclerView.FavoriteRecyclerAdapter;

public class FavoriteFragment extends Fragment {

    private FavoriteRecyclerAdapter favoriteRecyclerAdapter;
    private RecyclerView favoriteRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        // 리사이클러뷰에 어댑터와 레이아웃매니저 지정
        favoriteRecyclerView = root.findViewById(R.id.favorite_recyclerView);
        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        favoriteRecyclerAdapter = new FavoriteRecyclerAdapter(container.getContext());
        favoriteRecyclerView.setAdapter(favoriteRecyclerAdapter);
        return root;
    }
}
