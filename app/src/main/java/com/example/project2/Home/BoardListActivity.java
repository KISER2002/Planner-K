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
import com.example.project2.Adapter.BoardListAdapter;
import com.example.project2.Adapter.EditListAdapter;
import com.example.project2.R;
import com.example.project2.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardListActivity extends AppCompatActivity{
    private static final String PRODUCT_URL = "http://43.201.55.113/WorkSpaceList.php";
    private BoardListAdapter boardListAdapter;
    private RecyclerView recyclerView;
    private String getIdx, mId;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

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
                                LinearLayoutManager layoutManager = new LinearLayoutManager(BoardListActivity.this, LinearLayoutManager.VERTICAL, false);
                                boardListAdapter = new BoardListAdapter(workSpace, BoardListActivity.this);
                                recyclerView.setAdapter(boardListAdapter);
                                recyclerView.setLayoutManager(layoutManager);

                                boardListAdapter.setOnItemClickListener(new EditListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String list_idx, String title) {
                                        Intent intent = new Intent();
                                        intent.putExtra("list_idx",list_idx);
                                        intent.putExtra("title",title);
                                        setResult(RESULT_OK, intent); // [인텐트 종료 코드 지정]

                                        finish();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BoardListActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BoardListActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(BoardListActivity.this);
        requestQueue.add(stringRequest);
    }
}