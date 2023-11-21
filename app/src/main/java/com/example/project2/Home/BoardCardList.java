package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
import com.example.project2.Adapter.BoardListAdapter;
import com.example.project2.Adapter.CardListAdapter;
import com.example.project2.Adapter.EditListAdapter;
import com.example.project2.R;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.CardAttachRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoardCardList extends AppCompatActivity {
    private static final String BOARD_LIST = "http://43.201.55.113/SearchBoardList.php";
    private static final String CARD_LIST = "http://43.201.55.113/SearchCardList.php";
    private BoardListAdapter boardListAdapter;
    private CardListAdapter cardListAdapter;
    private RecyclerView recyclerView, recyclerView1;
    private String getIdx, mId;
    private TextView confirm_btn;
    private EditText search_et;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_card_list);

    }

    @Override
    public void onResume() {
        super.onResume();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        mId = user.get(sessionManager.ID);

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("pk");

        search_et = findViewById(R.id.search_et);
        confirm_btn = findViewById(R.id.confirm_btn);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText = search_et.getText().toString();

                if(getText.equals("")){
                    Toast.makeText(getApplicationContext(), "검색 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    loadBoard(mId, getText);
                    loadCard(mId, getText);
                }
            }
        });

    }

    private void loadBoard(String mID, String text) {
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BOARD_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            Intent intent = getIntent();
                            getIdx = intent.getStringExtra("pk");

                            ArrayList<WorkSpace> workSpace = new ArrayList<>();

                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                String getPk = product.getString("idx");
                                String getTitle = product.getString("title");
                                String getRoomImg = product.getString("image");
                                String getStar = product.getString("star");
                                String getCoverShow = product.getString("cover_show");

                                WorkSpace item = new WorkSpace();

                                item.setIdx(getPk);
                                item.setTitle(getTitle);
                                item.setRoomImg(getRoomImg);
                                item.setStar(getStar);
                                item.setCover_show(getCoverShow);

                                workSpace.add(item);

                                recyclerView = findViewById(R.id.board_rv);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(BoardCardList.this, LinearLayoutManager.VERTICAL, false);
                                boardListAdapter = new BoardListAdapter(workSpace, BoardCardList.this);
                                recyclerView.setAdapter(boardListAdapter);
                                recyclerView.setLayoutManager(layoutManager);

                                boardListAdapter.setOnItemClickListener(new EditListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String pk, String title) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                    if (success) {//저장에 성공한 경우
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                BoardCardList.this.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "카드가 첨부되었습니다.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {//저장에 실패한 경우
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        CardAttachRequest cardAttachRequest = new CardAttachRequest(getIdx, title, "board", pk, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(BoardCardList.this);
                                        queue.add(cardAttachRequest);
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BoardCardList.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BoardCardList.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mID", mID);
                params.put("text", text);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BoardCardList.this);
        requestQueue.add(stringRequest);
    }

    private void loadCard(String mID, String text) {
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CARD_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            Intent intent = getIntent();
                            getIdx = intent.getStringExtra("pk");

                            ArrayList<Card> card = new ArrayList<>();

                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                int getPk = product.getInt("pk");
                                int getList_idx = product.getInt("list_idx");
                                String getBno = product.getString("bno");
                                String getTitle = product.getString("title");

                                Card item = new Card(getPk, getList_idx, getBno, getTitle);
                                card.add(item);

                                recyclerView1 = findViewById(R.id.card_rv);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(BoardCardList.this, LinearLayoutManager.VERTICAL, false);
                                cardListAdapter = new CardListAdapter(card, BoardCardList.this);
                                recyclerView1.setAdapter(cardListAdapter);
                                recyclerView1.setLayoutManager(layoutManager);

                                cardListAdapter.setOnItemClickListener(new CardListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String pk, String title) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                    if (success) {//저장에 성공한 경우
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                BoardCardList.this.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "카드가 첨부되었습니다.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {//저장에 실패한 경우
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        CardAttachRequest cardAttachRequest = new CardAttachRequest(getIdx, title, "card", pk, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(BoardCardList.this);
                                        queue.add(cardAttachRequest);
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BoardCardList.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BoardCardList.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mID", mID);
                params.put("text", text);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BoardCardList.this);
        requestQueue.add(stringRequest);
    }
}
