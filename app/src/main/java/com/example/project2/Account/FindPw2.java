package com.example.project2.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.R;
import com.example.project2.VolleyRequest.ChangePwRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class FindPw2 extends AppCompatActivity {

    private EditText passwordEt, rePasswordEt;
    private Button confirmBtn;
    private String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        passwordEt = findViewById(R.id.find_pw_et);
        rePasswordEt = findViewById(R.id.find_re_pw_et);
        confirmBtn = findViewById(R.id.confirm_button);

        Intent intent = getIntent();
        id = intent.getStringExtra("userID");
        name = intent.getStringExtra("userName");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userPass = passwordEt.getText().toString();
                final String PassCk = rePasswordEt.getText().toString();

                //비밀번호가 공백일 시
                if (userPass.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    passwordEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //비밀번호 확인이 공백일 시
                else if (PassCk.equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    rePasswordEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } //비밀번호와 비밀번호 확인이 다를 경우
                else if (!userPass.equals(PassCk)) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    rePasswordEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                } else{
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//회원가입에 성공한 경우
                                    Toast.makeText(getApplicationContext(), "비밀번호 재설정에 성공하였습니다!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {//회원가입에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "비밀번호 재설정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    ChangePwRequest changePwRequest = new ChangePwRequest(id, userPass, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(FindPw2.this);
                    queue.add(changePwRequest);
                }
            }
        });

    }
}
