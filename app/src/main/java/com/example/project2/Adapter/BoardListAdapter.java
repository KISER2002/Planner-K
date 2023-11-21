package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.project2.Home.ListItem;
import com.example.project2.Home.WorkSpace;
import com.example.project2.R;

import java.util.ArrayList;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<WorkSpace> listItemList = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String list_idx, String title) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private EditListAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(EditListAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public BoardListAdapter(ArrayList<WorkSpace> list, Context mContext) {
        listItemList = list ;
        context = mContext;
    }

    @NonNull
    @Override
    public BoardListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workspace_item, parent, false);
        BoardListAdapter.ViewHolder holder = new BoardListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String idx = listItemList.get(position).getIdx();
        String title = listItemList.get(position).getTitle();
        String star = listItemList.get(position).getStar();
        String cover_show = listItemList.get(position).getCover_show();

        holder.tvTitle.setText(listItemList.get(position).getTitle());
        String profile = listItemList.get(position).getRoomImg();
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
                if (position != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, position, listItemList.get(position).getIdx(), listItemList.get(position).getTitle()) ;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemList.size();
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
