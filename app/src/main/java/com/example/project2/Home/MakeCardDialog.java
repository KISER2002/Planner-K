package com.example.project2.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.R;
import com.example.project2.VolleyRequest.CardRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MakeCardDialog extends Activity {
    private static String URL_LOADING = "http://43.201.55.113/lastCardPk.php";

    private int pk = 1, idx;
    private String  bno, positionStr;
    private int position;

    EditText card_name;
    Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_card_dialog);

        loadList();
    }

    private void loadList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                int getPk = product.getInt("pk".trim());

                                pk = getPk + 1;
                            }

                            Intent intent = getIntent();
                            idx = intent.getIntExtra("idx", 0);
                            bno = intent.getStringExtra("bno");
                            position = intent.getIntExtra("pos", 0);
                            positionStr = String.valueOf(position+1);

                            card_name = findViewById(R.id.card_name);
                            okBtn = findViewById(R.id.confirm_btn);

                            okBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //editText에 입력되어있는 값을 get(가져온다)해온다
                                    String cardName = card_name.getText().toString();

                                    //닉네임이 공백일 시
                                    if (cardName.equals("")) {
                                        Toast.makeText(getApplicationContext(), "카드 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        card_name.requestFocus(); //커서 이동
                                        //키보드 보이게 하는 부분
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                                    } else {
                                        //카드 DB
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
                                                        finish();
                                                    }
                                                    else{//저장에 실패한 경우
                                                        Toast.makeText(getApplicationContext(),"생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        CardRequest cardRequest = new CardRequest(positionStr, String.valueOf(idx), bno, cardName, "", "[]", responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(MakeCardDialog.this);
                                        queue.add(cardRequest);
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MakeCardDialog.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MakeCardDialog.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MakeCardDialog.this);
        requestQueue.add(stringRequest);
    }
}
