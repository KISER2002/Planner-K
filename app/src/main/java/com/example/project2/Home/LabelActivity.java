package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.CheckListAdapter;
import com.example.project2.Adapter.LabelAdapter;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.VolleyRequest.CardRequest;
import com.example.project2.VolleyRequest.DeleteLabelUserRequest;
import com.example.project2.VolleyRequest.LabelUserRequest;
import com.example.project2.databinding.ActivityLabelBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LabelActivity extends AppCompatActivity implements LabelAdapter.OnItemClickListener {
    private static final String PRODUCT_URL = "http://43.201.55.113/LabelList.php";

    ActivityLabelBinding binding;

    private ArrayList<Label> labelList;
    private LabelAdapter labelAdapter;

    private String cardPk, bno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLabelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        cardPk = intent.getStringExtra("pk");
        bno = intent.getStringExtra("bno");

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.addLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddLabelActivity.class);
                intent.putExtra("bno", bno);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = getIntent();
        cardPk = intent.getStringExtra("pk");
        bno = intent.getStringExtra("bno");

        binding.labelRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        labelList = new ArrayList<>();

        labelAdapter = new LabelAdapter(LabelActivity.this, labelList);
        binding.labelRv.setAdapter(labelAdapter);

        labelAdapter.setOnItemClickListener(new LabelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String label_pk, String isCheck) {
                if(isCheck.equals("true")) { // 체크 됐을 때
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//SeatEdit php에 response
                                boolean success = jasonObject.getBoolean("success");//SeatEdit php에 sucess
                                if (success) {//저장에 성공한 경우
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                }
                                else{//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    LabelUserRequest labelUserRequest = new LabelUserRequest(cardPk, label_pk, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LabelActivity.this);
                    queue.add(labelUserRequest);
                } if(isCheck.equals("false")) { // 체크가 해제 됐을 때
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//SeatEdit php에 response
                                boolean success = jasonObject.getBoolean("success");//SeatEdit php에 sucess
                                if (success) {//저장에 성공한 경우
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                }
                                else{//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    DeleteLabelUserRequest labelUserRequest = new DeleteLabelUserRequest(cardPk, label_pk, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LabelActivity.this);
                    queue.add(labelUserRequest);
                }
            }
        });

        labelAdapter.notifyDataSetChanged();

        loadList(cardPk, bno);

    }

    private void loadList(String sendPk, String sendBno) {

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

                                JSONObject item = array.getJSONObject(i);

                                String pk = item.getString("pk");
                                int idx = item.getInt("idx");
                                String bno = item.getString("bno");
                                String title = item.getString("title");
                                String color_str_kor = item.getString("color_str_kor");
                                String color_str = item.getString("color_str");
                                int color_int = item.getInt("color_int");
                                String check = item.getString("is_check");
                                Boolean is_check = false;
                                if(check.equals("1")){
                                    is_check = true;
                                } else {
                                    is_check = false;
                                }

                                Label label = new Label();

                                label.setPk(pk);
                                label.setIdx(idx);
                                label.setBno(bno);
                                label.setTitle(title);
                                label.setColorStrKor(color_str_kor);
                                label.setColorStr(color_str);
                                label.setColorInt(color_int);
                                label.setIsCheck(is_check);

                                labelList.add(label);
                                labelAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LabelActivity.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LabelActivity.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pk", sendPk);
                params.put("bno", sendBno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LabelActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onItemClick(String label_pk, String isCheck) {

    }
}
