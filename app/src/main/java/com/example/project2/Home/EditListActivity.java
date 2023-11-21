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
import com.example.project2.Adapter.ListAdapter;
import com.example.project2.Board.Board;
import com.example.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditListActivity extends AppCompatActivity{
    private static final String PRODUCT_URL = "http://43.201.55.113/LoadingList2.php";
    private EditListAdapter editListAdapter;
    private RecyclerView recyclerView;
    private String getIdx, getBno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("idx");
        getBno = intent.getStringExtra("bno");

        loadProducts(getBno);

    }

    private void loadProducts(String Bno) {

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

                            ArrayList<ListItem> itemListItem = new ArrayList<>();

                            JSONArray array = new JSONArray(response);
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject product = array.getJSONObject(i);

                                String pk = product.getString("pk");
                                String idx = product.getString("idx");
                                String bno = product.getString("bno");
                                String title = product.getString("title");
                                String getList = product.getString("list");

                                List<Card> cardList = new ArrayList<>();

                                if(!getList.equals("null")){
                                    JSONArray jsonArray = new JSONArray(getList);
                                    for(int i1 = 0; i1 < jsonArray.length(); i1++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i1);

                                        int card_idx = jsonObject.getInt("idx".trim());
                                        int card_list_idx = jsonObject.getInt("list_idx".trim());
                                        String card_bno = jsonObject.getString("bno".trim());
                                        String card_title = jsonObject.getString("title".trim());

                                        Card card = new Card(card_idx, card_list_idx, card_bno, card_title);
                                        cardList.add(card);
                                    }
                                }

                                ListItem item = new ListItem(pk, idx, bno, title, cardList);
                                itemListItem.add(item);

                                recyclerView = findViewById(R.id.edit_list_rv);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(EditListActivity.this, LinearLayoutManager.VERTICAL, false);
                                editListAdapter = new EditListAdapter(itemListItem, EditListActivity.this);
                                recyclerView.setAdapter(editListAdapter);
                                recyclerView.setLayoutManager(layoutManager);

                                editListAdapter.setOnItemClickListener(new EditListAdapter.OnItemClickListener() {
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
                            Toast.makeText(EditListActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditListActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bno", Bno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(EditListActivity.this);
        requestQueue.add(stringRequest);
    }
}