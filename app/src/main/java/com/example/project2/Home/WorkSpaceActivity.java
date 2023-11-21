package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.ListAdapter;
import com.example.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkSpaceActivity extends AppCompatActivity {
    private static final String LIST_URL = "http://43.201.55.113/LoadingList.php";
    private static final String CARD_URL = "http://43.201.55.113/LoadingCard.php";

    private String idx, title;
    TextView title_tv;
    ImageButton add_list_btn;

    private ArrayList<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace);

        cardList = new ArrayList<Card>();

        Intent intent = getIntent();
        idx = intent.getStringExtra("room_idx");
        title = intent.getStringExtra("room_title");

        title_tv = findViewById(R.id.title_tv);
        add_list_btn = findViewById(R.id.add_list_btn);

        title_tv.setText(title);
        add_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), MakeListDialog.class);
//                intent.putExtra("room_idx", idx);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = getIntent();
        idx = intent.getStringExtra("room_idx");
        title = intent.getStringExtra("room_title");

        loadList(idx);

    }

    private void loadList(String getBno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            List<ListItem> itemListItem = new ArrayList<>();

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                String getPk = product.getString("pk");
                                String getIdx = product.getString("idx");
                                String getBno = product.getString("bno");
                                String getTitle = product.getString("title");
                                String getList = product.getString("list");

                                List<Card> cardList = new ArrayList<>();

                                if(!getList.equals("null")){
                                    JSONArray jsonArray = new JSONArray(getList);
                                    for(int i1 = 0; i1 < jsonArray.length(); i1++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i1);

                                        int idx = jsonObject.getInt("idx".trim());
                                        int list_idx = jsonObject.getInt("list_idx".trim());
                                        String bno = jsonObject.getString("bno".trim());
                                        String title = jsonObject.getString("title".trim());

                                        Card card = new Card(idx, list_idx, bno, title);
                                        cardList.add(card);
                                    }
                                }
                                ListItem item = new ListItem(getPk, getIdx, getBno, getTitle, cardList);
                                itemListItem.add(item);

                                // 상위 리사이클러뷰 설정
                                RecyclerView rvItem = findViewById(R.id.rv_item);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(WorkSpaceActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                ListAdapter itemAdapter = new ListAdapter(WorkSpaceActivity.this, itemListItem);
                                rvItem.setAdapter(itemAdapter);
                                rvItem.setLayoutManager(layoutManager);

//                                rvItem.scrollToPosition(array.length()-1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WorkSpaceActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(WorkSpaceActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bno", getBno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(WorkSpaceActivity.this);
        requestQueue.add(stringRequest);
    }
}