package com.example.project2.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.Board.BoardImage;
import com.example.project2.Board.BoardView;
import com.example.project2.R;

public class ViewHolderPage extends RecyclerView.ViewHolder {
    private Activity context = null;

    private ImageView imageView;

    BoardImage data;

    ViewHolderPage(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.view_image);
    }

    public void onBind(Activity context, BoardImage data){
        this.context = context;
        this.data = data;

        Glide.with(context).load("http://43.201.55.113" + data.getImage()).into(imageView);
//        imageView.setImageResource(R.drawable.profile_img);
    }
}
