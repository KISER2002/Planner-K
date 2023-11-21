package com.example.project2.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2.Home.ListItem;
import com.example.project2.Home.MakeCardDialog;
import com.example.project2.Home.WorkSpaceActivity;
import com.example.project2.R;

import java.io.Serializable;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ListItem> itemList;
    private Activity context = null;

    public ListAdapter(Activity context, List<ListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, @SuppressLint("RecyclerView") int i) {
        ListItem item = itemList.get(i);
        itemViewHolder.tvItemTitle.setText(item.getTitle());
        itemViewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MakeCardDialog.class);
                intent.putExtra("idx", item.getIdx());
                intent.putExtra("bno", item.getBno());
                intent.putExtra("pos", item.getCardList().size());
                context.startActivity(intent);
            }
        });

        // 자식 레이아웃 매니저 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(item.getCardList().size());

        // 자식 어답터 설정
        CardAdapter subItemAdapter = new CardAdapter(item.getCardList());

        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(subItemAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemTitle;
        private ImageButton addBtn;
        private RecyclerView rvSubItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            // 부모 타이틀
            tvItemTitle = itemView.findViewById(R.id.tv_item_title);
            addBtn = itemView.findViewById(R.id.add_card_btn);
            // 자식아이템 영역
            rvSubItem = itemView.findViewById(R.id.rv_sub_item);
        }
    }
}
