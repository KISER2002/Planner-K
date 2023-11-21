package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.Home.Card;
import com.example.project2.Home.CardCheckList;
import com.example.project2.Home.WorkSpace;
import com.example.project2.ItemTouchHelper.ItemMoveCallback;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<Card> listItemList = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String list_idx, String title) ;
    }
    // 리스너 객체 참조를 저장하는 변수
    private CardListAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(CardListAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public CardListAdapter(ArrayList<Card> list, Context mContext) {
        listItemList = list ;
        context = mContext;
    }

    @NonNull
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_item, parent, false);
        CardListAdapter.ViewHolder holder = new CardListAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int idx = listItemList.get(position).getIdx();
        String title = listItemList.get(position).getTitle();

        holder.tvTitle.setText(title);

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(v, position, String.valueOf(idx), listItemList.get(position).getTitle()) ;
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

        protected TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.tvTitle = itemView.findViewById(R.id.item_tv);
        }
    }
}
