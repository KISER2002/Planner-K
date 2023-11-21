package com.example.project2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Home.CardCount;
import com.example.project2.R;

import java.util.ArrayList;

public class EditPositionAdapter extends RecyclerView.Adapter<EditPositionAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<CardCount> listItemList = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, int idx) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private EditPositionAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(EditPositionAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public EditPositionAdapter(ArrayList<CardCount> list, Context mContext) {
        listItemList = list ;
        context = mContext;
    }

    @NonNull
    @Override
    public EditPositionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_item, parent, false);
        EditPositionAdapter.ViewHolder holder = new EditPositionAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditPositionAdapter.ViewHolder holder, int position) {

        holder.itemTv.setText(String.valueOf(listItemList.get(position).getIdx()));
        holder.itemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, position, listItemList.get(position).getIdx()) ;
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
