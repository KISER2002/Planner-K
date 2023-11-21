package com.example.project2.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.BoardCopyRequest;
import com.example.project2.databinding.ActivityBoardCopyBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CopyBoard extends Activity {

    ActivityBoardCopyBinding binding;

    String room_idx, image;
    String keep_card = "o";

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardCopyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        Intent intent = getIntent();
        room_idx = intent.getStringExtra("room_idx");
        image = intent.getStringExtra("image");

        binding.keepCardSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keep_card.equals("o")){
                    keep_card = "x";
                } else{
                    keep_card = "o";
                }
            }
        });

        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.title.getText().toString();
                if(title.equals("")){
                    Toast.makeText(getApplicationContext(),"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                } else {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                if (success) {//좋아요 클릭에 성공한 경우
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(),"보드가 복사되었습니다.\n홈에서 확인해주세요!",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                    finish();
                                }
                                else{//회원가입에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"복사에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    BoardCopyRequest boardCopyRequest = new BoardCopyRequest(room_idx, title, mId, image, keep_card, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(CopyBoard.this);
                    queue.add(boardCopyRequest);
                }
            }
        });

    }



}
