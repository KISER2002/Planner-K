package com.example.project2.Home;

import android.app.Activity;
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
import com.example.project2.Adapter.EditListAdapter;
import com.example.project2.Adapter.EditPositionAdapter;
import com.example.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPositionActivity extends AppCompatActivity{
    private static final String PRODUCT_URL = "http://43.201.55.113/CardCountList.php";
    private EditPositionAdapter editListAdapter;
    private RecyclerView recyclerView;
    private String getIdx, getListIdx, getOldListIdx;
    private int tempIdx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_position);

    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("idx");
        getListIdx = intent.getStringExtra("list_idx");
        getOldListIdx = intent.getStringExtra("old_list_idx");

        loadProducts(getListIdx);

    }

    private void loadProducts(String list_idx) {

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
                            ArrayList<CardCount> cardCount = new ArrayList<>();

                            JSONArray array = new JSONArray(response);
                            //traversing through all the object

                            Intent intent = getIntent();
                            getIdx = intent.getStringExtra("idx");
                            getListIdx = intent.getStringExtra("list_idx");
                            getOldListIdx = intent.getStringExtra("old_list_idx");

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                int idx = product.getInt("idx");
                                tempIdx = idx;

                                CardCount cardCount1 = new CardCount(idx);
                                cardCount.add(cardCount1);
                            }
                            if(getListIdx.equals(getOldListIdx)){

                            }else{
                                int lastIdx = tempIdx + 1;
                                CardCount cardCount1 = new CardCount(lastIdx);
                                cardCount.add(cardCount1);
                            }

                            recyclerView = findViewById(R.id.edit_position_rv);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(EditPositionActivity.this, LinearLayoutManager.VERTICAL, false);
                            editListAdapter = new EditPositionAdapter(cardCount, EditPositionActivity.this);
                            recyclerView.setAdapter(editListAdapter);
                            recyclerView.setLayoutManager(layoutManager);

                            editListAdapter.setOnItemClickListener(new EditPositionAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position, int idx) {
                                    Intent intent = new Intent();
                                    intent.putExtra("idx",String.valueOf(idx));
                                    setResult(RESULT_OK, intent); // [인텐트 종료 코드 지정]

                                    finish();
                                }
                            });



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditPositionActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditPositionActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("list_idx", list_idx);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditPositionActivity.this);
        requestQueue.add(stringRequest);
    }
}