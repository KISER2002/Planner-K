package com.example.project2.Forget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.HomeActivity;
import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.SendMail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetId extends AppCompatActivity {
    ForgetActivity FA = (ForgetActivity) ForgetActivity.forgetActivity;

    private Button findBtn;
    private EditText nameEt, emailEt, emailConfirmEt;
    private Button emailSendBtn, emailCheckBtn;
    private String id, name;
    private static String URL_ID_FIND = "http://43.201.55.113/IdFind.php";

    boolean isEmailCheck; // 이메일 인증 완료 여부

    private String emailCode; // 이메일 인증 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_id);

        findBtn = findViewById(R.id.find_btn);
        nameEt = findViewById(R.id.forget_name_et);
        emailEt = findViewById(R.id.forget_email_et);
        emailConfirmEt = findViewById(R.id.join_email_check);
        emailSendBtn = findViewById(R.id.email_send_button);
        emailCheckBtn = findViewById(R.id.email_check_button);

        //이메일 권한 허용
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        //이메일 전송 버튼
        emailSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String userEmail = emailEt.getText().toString();

                if(userEmail.equals("")){
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                } else if( !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(getApplicationContext(), "이메일 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }else {
                    SendMailForget mailServer = new SendMailForget();
                    emailCode = mailServer.emailCode;
                    mailServer.sendSecurityCode(getApplicationContext(), emailEt.getText().toString());
                    emailEt.setEnabled(false);
                    emailEt.setBackgroundResource(R.drawable.edit_text_disabled);
                    emailConfirmEt.setEnabled(true);
                    emailConfirmEt.setBackgroundResource(R.drawable.edit_text_background);
                    emailSendBtn.setText("재전송");
                }
            }
        });

        //이메일 확인 버튼
        emailCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailConfirm = emailConfirmEt.getText().toString();

                if(emailConfirm.equals("")){
                    Toast.makeText(getApplicationContext(), "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailConfirmEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                } else if(!emailConfirm.equals(emailCode)){
                    Toast.makeText(getApplicationContext(), "인증번호가 틀렸습니다.\n 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                    emailConfirmEt.requestFocus(); //커서 이동
                    //키보드 보이게 하는 부분
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }else {
                    Toast.makeText(getApplicationContext(), "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    emailConfirmEt.setEnabled(false);
                    emailConfirmEt.setBackgroundResource(R.drawable.edit_text_disabled);
                    emailSendBtn.setClickable(false);
                    emailSendBtn.setText("인증 완료");
                    emailCheckBtn.setClickable(false);
                    emailCheckBtn.setText("인증 완료");
                    isEmailCheck = true;
                }
            }
        });


        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // EditText에 현재 입력되어있는 값을 get(가져온다)해온다.
                String name = nameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();

                if(!name.isEmpty() && !email.isEmpty() && isEmailCheck){
                    Find(name, email);
                }
                else if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 인증을 완료 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Find(String userName, String userEmail){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ID_FIND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if(success.equals("1")){ // 로그인 성공
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    id = object.getString("userID".trim());
                                    name = object.getString("userName".trim());

                                    Intent intent = new Intent(ForgetId.this, FindId.class);
                                    intent.putExtra("userID", id);
                                    intent.putExtra("userName", name);
                                    startActivity(intent);
                                    finish();
                                    FA.finish();

                                }
                            } else { // 로그인 실패
                                Toast.makeText(getApplicationContext(),"유효하지 않은 입력값 입니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"유효하지 않은 입력값 입니다.",Toast.LENGTH_SHORT).show();
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
                params.put("userName", userName);
                params.put("userEmail", userEmail);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
