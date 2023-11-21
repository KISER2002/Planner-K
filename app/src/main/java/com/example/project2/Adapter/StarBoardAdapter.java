package com.example.project2.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.A.Test;
import com.example.project2.Home.WorkSpace;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import java.util.ArrayList;

public class StarBoardAdapter extends RecyclerView.Adapter<StarBoardAdapter.ViewHolder> {

    private Activity context = null;
    private ArrayList<WorkSpace> workspaceList = null;

    private OnListItemSelectedInterface mListener;

    public StarBoardAdapter(Activity context, ArrayList<WorkSpace> chatRoomList, OnListItemSelectedInterface mListener) {
        this.workspaceList = chatRoomList;
        this.context = context;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public StarBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workspace_item, parent, false);
        StarBoardAdapter.ViewHolder holder = new StarBoardAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StarBoardAdapter.ViewHolder holder, int position) {

        String idx = workspaceList.get(position).getIdx();
        String title = workspaceList.get(position).getTitle();
        String star = workspaceList.get(position).getStar();
        String cover_show = workspaceList.get(position).getCover_show();

        holder.tvTitle.setText(workspaceList.get(position).getTitle());
        String profile = workspaceList.get(position).getRoomImg();
        if(profile.equals("basic_image")){
            Glide.with(context).load(R.drawable.null_room).override(50, 50).into(holder.room_image);
        }else {
            Glide.with(context).load("http://43.201.55.113" + profile).override(50, 50).into(holder.room_image);
        }
        if(star.equals("1")){
            holder.star_iv.setVisibility(View.VISIBLE);
        } else{
            holder.star_iv.setVisibility(View.GONE);
        }

        holder.user_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Test.class);
                intent.putExtra("room_idx", idx);
                intent.putExtra("room_title", title);
                intent.putExtra("cover_show", cover_show);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return workspaceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected LinearLayout user_item;
        protected TextView tvTitle;
        protected ImageView room_image, star_iv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.user_item = itemView.findViewById(R.id.workspace_item);
            this.tvTitle = itemView.findViewById(R.id.title_tv);//아이템에 들어갈 작성자
            this.room_image = itemView.findViewById(R.id.thumbnail_img);//아이템에 들어갈 썸네일 이미지
            this.star_iv = itemView.findViewById(R.id.start_iv);

        }
    }
}
