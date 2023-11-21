/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.project2.A;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project2.Home.Card;
import com.example.project2.Home.CardView;
import com.example.project2.Home.Label;
import com.example.project2.Home.MakeCardDialog;
import com.example.project2.R;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.woxthebox.draglistview.DragItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ItemAdapter extends DragItemAdapter<Pair<Long, String>, ItemAdapter.ViewHolder> {
    private static String LABEL_LOADING = "http://43.201.55.113/CardLabelList.php";

    private ArrayList<Label> labelList;

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    private Activity mContext = null;

    ItemAdapter(Activity context, ArrayList<Pair<Long, String>> list, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mContext = context;
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        String stringValue = mItemList.get(position).second;
        try {
            JSONObject obj = new JSONObject(stringValue);
            String pk =obj.getString("pk");
            String idx = obj.getString("idx");
            String list_idx = obj.getString("list_idx");
            String bno = obj.getString("bno");
            String title = obj.getString("title");
            String start_time = obj.getString("start_time");
            String end_time = obj.getString("end_time");
            String date_checked = obj.getString("date_checked");
            String cover = obj.getString("cover");
            String archive = obj.getString("archive");
            String get_label_list = obj.getString("label_list");


                if(!get_label_list.equals("null")){
                    labelList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(get_label_list);
                    for(int i1 = 0; i1 < jsonArray.length(); i1++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i1);

                        String label_pk = jsonObject.getString("label_pk".trim());
                        int label_idx = jsonObject.getInt("idx".trim());
                        String label_title = jsonObject.getString("title".trim());
                        String color_str_kor = jsonObject.getString("color_str_kor".trim());
                        String color_str = jsonObject.getString("color_str".trim());
                        int color_int = jsonObject.getInt("color_int".trim());

                        Label label = new Label();

                        label.setPk(label_pk);
                        label.setIdx(label_idx);
                        label.setTitle(label_title);
                        label.setColorStrKor(color_str_kor);
                        label.setColorStr(color_str);
                        label.setColorInt(color_int);

                        labelList.add(label);
                    }
                    FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext);
                    holder.label_rv.setLayoutManager(flexboxLayoutManager);

                    RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerViewAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View itemView = LayoutInflater.from(mContext).inflate(R.layout.card_label_item_home, parent, false);
                            return new RecyclerView.ViewHolder(itemView) {
                            };
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            LinearLayout label_item = holder.itemView.findViewById(R.id.label_item);
                            if(labelList.get(position).getColorStr().equals("no_color")){
                                label_item.setVisibility(View.GONE);
                            } else {
                                label_item.setBackgroundColor(labelList.get(position).getColorInt());
                            }
                        }

                        @Override
                        public int getItemCount() {
                            return labelList.size();
                        }
                    };
                    holder.label_rv.setAdapter(recyclerViewAdapter);

                } else {

                }

                holder.mText.setText(title);
                holder.itemView.setTag(mItemList.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, CardView.class);
                        intent.putExtra("pk", pk);
                        intent.putExtra("idx", idx);
                        intent.putExtra("list_idx", list_idx);
                        intent.putExtra("bno", bno);
                        intent.putExtra("title", title);
                        intent.putExtra("start_time", start_time);
                        intent.putExtra("end_time", end_time);
                        mContext.startActivity(intent);
                    }
                });

                if(cover.equals("")){
                    holder.cover_iv.setVisibility(View.GONE);
                } if(cover.equals("1")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#7BC86C"));
                } if(cover.equals("2")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#F5DD29"));
                } if(cover.equals("3")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#FFAF3F"));
                } if(cover.equals("4")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#EF7564"));
                } if(cover.equals("5")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#CD8DE5"));
                } if(cover.equals("6")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#5BA4CF"));
                } if(cover.equals("7")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#29CCE5"));
                } if(cover.equals("8")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#6DECA9"));
                } if(cover.equals("9")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#FF8ED4"));
                } if(cover.equals("10")){
                    holder.cover_iv.setBackgroundColor(Color.parseColor("#172B4D"));
                } if(cover.startsWith("/Images/")) {
                    Glide.with(mContext).load("http://43.201.55.113" + cover).into(holder.cover_iv);
                }

                String[] startTimeArr = start_time.split("일 ");
                String[] endTimeArr = end_time.split("일 ");

                if(start_time.equals("") && end_time.equals("")){ // 시작, 종료 시간이 없을 떄
                    holder.time_layout.setVisibility(View.GONE);
                }

                if(!start_time.equals("") && end_time.equals("")){ // 시작 시간만 있을 때
                    holder.time_tv.setText("시작일: " + startTimeArr[0] + "일");
                }

                if(start_time.equals("") && !end_time.equals("")){ // 종료 시간만 있을 때
                    holder.time_tv.setText("종료일: " + endTimeArr[0] + "일");
                }

                if(!start_time.equals("") && !end_time.equals("")){ // 시작, 종료 시간이 있을 때
                    holder.time_tv.setText(startTimeArr[0] + "일 - " + endTimeArr[0] + "일");
                }

                if(date_checked.equals("1")){
                    holder.time_tv.setTextColor(Color.WHITE);
                    holder.time_layout.setBackgroundColor(Color.parseColor("#8BC34A"));
                }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText, time_tv;
        LinearLayout time_layout;
        RecyclerView label_rv;
        ImageView cover_iv;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);
            label_rv = itemView.findViewById(R.id.label_rv);
            time_tv = itemView.findViewById(R.id.time_tv);
            time_layout = itemView.findViewById(R.id.time_layout);
            cover_iv = itemView.findViewById(R.id.cover_image_view);
        }

        @Override
        public void onItemClicked(View view) {
//            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
//            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
