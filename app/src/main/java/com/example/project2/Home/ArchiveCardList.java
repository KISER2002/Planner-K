package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.ArchiveCardAdapter;
import com.example.project2.Adapter.EditPositionAdapter;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;
import com.example.project2.VolleyRequest.ArchiveAllCardRequest;
import com.example.project2.VolleyRequest.ArchiveCardRequest;
import com.example.project2.VolleyRequest.UnArchiveCardRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArchiveCardList extends AppCompatActivity {
    private static final String PRODUCT_URL = "http://43.201.55.113/ArchiveCardList.php";

    private String bno;

    RecyclerView recyclerView;

    ArrayList<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive_card_list);

        Intent intent = getIntent();
        bno = intent.getStringExtra("bno");
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        bno = intent.getStringExtra("bno");

        cardList = new ArrayList<>();

        loadProducts(bno);
    }

    private void loadProducts(String bno) {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                int getIdx = product.getInt("pk");
                                String getBno = product.getString("bno");
                                int getList_idx = product.getInt("list_idx");
                                String getTitle = product.getString("title");

                                Card card = new Card(getIdx, getList_idx, getBno, getTitle);

                                cardList.add(card);

                                ArchiveCardAdapter adapter = new ArchiveCardAdapter(cardList, ArchiveCardList.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                adapter.setOnItemClickListener(new ArchiveCardAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, int idx) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                    boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                    if (success) {//정보수정에 성공한 경우
                                                        Toast.makeText(ArchiveCardList.this, "보관이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                ArchiveCardList.this.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                    }
                                                    else{
                                                        Toast.makeText(ArchiveCardList.this,"보관에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        UnArchiveCardRequest archiveAllCardRequest = new UnArchiveCardRequest(String.valueOf(idx), responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(ArchiveCardList.this);
                                        queue.add(archiveAllCardRequest);

                                        finish();//인텐트 종료
                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                        Intent intent = getIntent(); //인텐트
                                        startActivity(intent); //액티비티 열기
                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bno", bno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
