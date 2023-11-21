package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Home.CardCheckList;
import com.example.project2.ItemTouchHelper.CheckListItemMoveCallback;
import com.example.project2.R;

import java.util.ArrayList;
import java.util.Collections;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> implements CheckListItemMoveCallback.ItemTouchHelperContract {
    private Context mContext = null;
    private ArrayList<CardCheckList> cardCheckList = new ArrayList<CardCheckList>();

    private OnItemClick mCallback;

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(cardCheckList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(cardCheckList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundResource(R.drawable.border_gray);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundResource(R.drawable.border);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, String isCheck) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private CheckListAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(CheckListAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public CheckListAdapter(Context context, ArrayList<CardCheckList> cardCheckList, OnItemClick listener) {
        this.cardCheckList = cardCheckList;
        this.mContext = context;
        this.mCallback = listener;
    }

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public CheckListAdapter(ArrayList<CardCheckList> list, Context context) {
        cardCheckList = list ;
        mContext = context;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView addPlanTv;
        protected CheckBox checkBox;
        protected ImageButton planRemoveBtn;
        View rowView;

        ViewHolder(View itemView) {
            super(itemView) ;

            rowView = itemView;

            addPlanTv = itemView.findViewById(R.id.add_plan_tv);
            checkBox = itemView.findViewById(R.id.add_plan_check_box);
            planRemoveBtn = itemView.findViewById(R.id.plan_minus_button);
        }
    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    // LayoutInflater - XML에 정의된 Resource(자원) 들을 View의 형태로 반환.
    @Override
    public CheckListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;    // context에서 LayoutInflater 객체를 얻는다.
        View view = inflater.inflate(R.layout.add_card_list_item, parent, false) ;	// 리사이클러뷰에 들어갈 아이템뷰의 레이아웃을 inflate.
        CheckListAdapter.ViewHolder vh = new CheckListAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(CheckListAdapter.ViewHolder holder, int position) {
        holder.addPlanTv.setText(cardCheckList.get(position).getContent());

        if(cardCheckList.get(position).getIsEdit()) {
            holder.checkBox.setVisibility(View.GONE);
            holder.planRemoveBtn.setVisibility(View.VISIBLE);
        } else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.planRemoveBtn.setVisibility(View.GONE);
        }

        Boolean check = cardCheckList.get(position).getIsCheck();
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardCheckList.get(position).getIsCheck()){
                    cardCheckList.get(position).setIsCheck(false);
                    int pos = holder.getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, "false") ;
                        }
                    }
                } else {
                    cardCheckList.get(position).setIsCheck(true);
                    int pos = holder.getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, "true") ;
                        }
                    }
                }
                notifyDataSetChanged();

            }
        });

        if(check){
            holder.checkBox.setChecked(true);
            holder.addPlanTv.setPaintFlags(holder.addPlanTv.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            holder.addPlanTv.setTextColor(Color.GRAY);
        }if(!check){
            holder.checkBox.setChecked(false);
            holder.addPlanTv.setPaintFlags(0);
            holder.addPlanTv.setTextColor(Color.BLACK);
        }

        holder.planRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardCheckList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return cardCheckList.size() ;
    }
}
