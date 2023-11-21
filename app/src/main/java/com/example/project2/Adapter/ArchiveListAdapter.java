package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Home.Card;
import com.example.project2.Home.ListItem;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import java.util.ArrayList;

public class ArchiveListAdapter extends RecyclerView.Adapter<ArchiveListAdapter.ViewHolder>{

    private Context context = null;
    private ArrayList<ListItem> likeList = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String idx) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private ArchiveListAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ArchiveListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public ArchiveListAdapter(ArrayList<ListItem> list, Context mContext) {
        likeList = list ;
        context = mContext;
    }

    @NonNull
    @Override
    public ArchiveListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_card_item, parent, false);
        ArchiveListAdapter.ViewHolder holder = new ArchiveListAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(likeList.get(position).getTitle());
        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, position, likeList.get(position).getPk()) ;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != likeList ? likeList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView title, cancel_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.title = itemView.findViewById(R.id.title);
            this.cancel_btn = itemView.findViewById(R.id.cancel_button);

        }
    }
}
