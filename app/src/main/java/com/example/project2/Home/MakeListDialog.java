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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.R;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.ListRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MakeListDialog extends Activity {

    private String bno;
    private int idx;

    EditText list_name;
    Button okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_list_dialog);

        Intent intent = getIntent();
        idx = intent.getIntExtra("idx", 1);
        bno = intent.getStringExtra("room_idx");

        list_name = findViewById(R.id.list_name);
        okBtn = findViewById(R.id.confirm_btn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editText에 입력되어있는 값을 get(가져온다)해온다
                String listName = list_name.getText().toString();

                //닉네임이 공백일 시
                if (listName.equals("")) {
                    Toast.makeText(getApplicationContext(), "리스트 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    list_name.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//회원가입에 성공한 경우
                                    finish();
                                } else {//회원가입에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "리스트 생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    ListRequest listRequest = new ListRequest(String.valueOf(idx) ,bno, listName, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MakeListDialog.this);
                    queue.add(listRequest);
                }
            }
        });
    }
}
