package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.Board.Board;
import com.example.project2.Board.BoardView;
import com.example.project2.Board.CommentList;
import com.example.project2.Home.AddLabelActivity;
import com.example.project2.Home.EditLabelActivity;
import com.example.project2.Home.Label;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import java.util.ArrayList;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHolder> {

    private Activity context = null;
    private ArrayList<Label> labelList = null;

    private OnItemClick mCallback;

    public LabelAdapter(Activity context, ArrayList<Label> labelList) {
        this.labelList = labelList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(String label_pk, String isCheck) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private LabelAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(LabelAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public LabelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 미리 만들어 놓은 item_post.xml 기입
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_item, parent, false);
        LabelAdapter.ViewHolder holder = new LabelAdapter.ViewHolder(view) ;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LabelAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvTitle.setText(labelList.get(position).getTitle());
        holder.label_item.setBackgroundColor(labelList.get(position).getColorInt());
        holder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditLabelActivity.class);
                intent.putExtra("pk", labelList.get(position).getPk());
                intent.putExtra("idx", labelList.get(position).getIdx());
                intent.putExtra("title", labelList.get(position).getTitle());
                intent.putExtra("colorStrKor", labelList.get(position).getColorStrKor());
                intent.putExtra("colorStr", labelList.get(position).getColorStr());
                intent.putExtra("colorInt", labelList.get(position).getColorInt());
                context.startActivity(intent);
            }
        });

        if(labelList.get(position).getColorInt() == -3190467 || labelList.get(position).getColorInt() == -5739327
        || labelList.get(position).getColorInt() == -16618841 || labelList.get(position).getColorInt() == -11509895
        || labelList.get(position).getColorInt() == -13351581 || labelList.get(position).getColorInt() == -16179646
        || labelList.get(position).getColorInt() == -9342607){
            holder.tvTitle.setTextColor(Color.WHITE);
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        } else {
            holder.tvTitle.setTextColor(Color.BLACK);
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }

        Boolean check = labelList.get(position).getIsCheck();
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(labelList.get(position).getIsCheck()){
                    labelList.get(position).setIsCheck(false);
                    int pos = holder.getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(labelList.get(position).getPk(), "false") ;
                        }
                    }
                } else {
                    labelList.get(position).setIsCheck(true);
                    int pos = holder.getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(labelList.get(position).getPk(), "true") ;
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        if(check){
            holder.checkBox.setChecked(true);
        }if(!check){
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox checkBox;
        protected TextView tvTitle;
        protected ImageButton edit_button;
        protected LinearLayout label_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //뷰홀더에 필요한 아이템데이터 findview
            this.checkBox = itemView.findViewById(R.id.label_checkbox);//라벨 체크 박스
            this.tvTitle = itemView.findViewById(R.id.title_tv);//아이템에 들어갈 제목
            this.label_item = itemView.findViewById(R.id.label_item);//아이템 전체 레이아웃
            this.edit_button = itemView.findViewById(R.id.edit_button);//라벨 수정 버튼
        }
    }
}
