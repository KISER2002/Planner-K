package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.example.project2.Board.BoardView;
import com.example.project2.Board.CommentList;
import com.example.project2.Home.CardCheckList;
import com.example.project2.Home.ListItem;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import java.util.ArrayList;

public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<ListItem> listItemList = null;

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
    public EditListAdapter(ArrayList<ListItem> list, Context mContext) {
        listItemList = list ;
        context = mContext;
    }

    @NonNull
    @Override
    public EditListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_item, parent, false);
        EditListAdapter.ViewHolder holder = new EditListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.itemTv.setText(listItemList.get(position).getTitle());
        holder.itemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, position, listItemList.get(position).getPk(), listItemList.get(position).getTitle()) ;
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

        protected TextView itemTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.itemTv = itemView.findViewById(R.id.item_tv);//아이템에 들어갈 제목
        }
    }
}
