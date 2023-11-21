package com.example.project2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Board.BoardImage;
import com.example.project2.R;

import java.util.ArrayList;

public class BoardImageViewHolderAdapter extends RecyclerView.Adapter<ViewHolderPage> {

    private Activity context = null;
    private ArrayList<BoardImage> listData;

    public BoardImageViewHolderAdapter(Activity context, ArrayList<BoardImage> data){
        this.context = context;
        this.listData = data;
    }

    @NonNull
    @Override
    public ViewHolderPage onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.frag_board_image_item, parent, false);
        return new ViewHolderPage(view);
    }



    @Override
    public void onBindViewHolder(ViewHolderPage holder, int position) {
        if(holder instanceof ViewHolderPage){
            ViewHolderPage viewHolder = (ViewHolderPage) holder;
            viewHolder.onBind(context ,listData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
