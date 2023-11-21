package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.project2.Adapter.EditListAdapter;
import com.example.project2.R;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.ListCopyRequest;
import com.example.project2.VolleyRequest.ListMoveRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListMoveActivity extends AppCompatActivity {
    private static final String PRODUCT_URL = "http://43.201.55.113/WorkSpaceList.php";
    private BoardListAdapter boardListAdapter;
    private RecyclerView recyclerView;
    private String getIdx, getBno, mId;
    TextView title_tv;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        title_tv = findViewById(R.id.title_tv);
        title_tv.setText(" 리스트 이동");

    }

    @Override
    public void onResume() {
        super.onResume();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        mId = user.get(sessionManager.ID);

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("idx");

        loadProducts(mId);

    }

    private void loadProducts(String mID) {

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
                            Intent intent = getIntent();
                            getIdx = intent.getStringExtra("idx");
                            getBno = intent.getStringExtra("bno");

                            ArrayList<WorkSpace> workSpace = new ArrayList<>();

                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                String pk = product.getString("idx");
                                String title = product.getString("title");
                                String image = product.getString("image");
                                String star = product.getString("star");
                                String cover_show = product.getString("cover_show");

                                WorkSpace item = new WorkSpace();
                                item.setIdx(pk);
                                item.setTitle(title);
                                item.setRoomImg(image);
                                item.setStar(star);
                                item.setCover_show(cover_show);
                                workSpace.add(item);

                                recyclerView = findViewById(R.id.edit_board_rv);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(ListMoveActivity.this, LinearLayoutManager.VERTICAL, false);
                                boardListAdapter = new BoardListAdapter(workSpace, ListMoveActivity.this);
                                recyclerView.setAdapter(boardListAdapter);
                                recyclerView.setLayoutManager(layoutManager);

                                boardListAdapter.setOnItemClickListener(new EditListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String list_idx, String title) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                    if (success) {//저장에 성공한 경우
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                        Toast.makeText(getApplicationContext(),"리스트가 이동되었습니다.",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {//저장에 실패한 경우
                                                        Toast.makeText(getApplicationContext(), "이동에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        ListMoveRequest cardPosEditRequest = new ListMoveRequest(getIdx, list_idx, getBno, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(ListMoveActivity.this);
                                        queue.add(cardPosEditRequest);
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ListMoveActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListMoveActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mID", mID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ListMoveActivity.this);
        requestQueue.add(stringRequest);
    }
}
