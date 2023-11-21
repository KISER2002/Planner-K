package com.example.project2.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.Board.CommentDeleteDialog;
import com.example.project2.R;
import com.example.project2.VolleyRequest.AttachDeleteRequest;
import com.example.project2.VolleyRequest.CommentDeleteRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class AttachDeleteDialog extends Activity {

    Button noBtn, yesBtn;
    String getIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.frag_board_comment_delete_dialog);
        noBtn = findViewById(R.id.noBtn);
        yesBtn = findViewById(R.id.yesBtn);

        Intent intent = getIntent();
        getIdx = intent.getStringExtra("idx");

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                            if (success) {//정보수정에 성공한 경우
                                Toast.makeText(AttachDeleteDialog.this, "첨부 파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    public void run() {
                                        AttachDeleteDialog.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();
                            }
                            else{
                                Toast.makeText(AttachDeleteDialog.this,"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                AttachDeleteRequest attachDeleteRequest = new AttachDeleteRequest(getIdx, responseListener);
                RequestQueue queue = Volley.newRequestQueue(AttachDeleteDialog.this);
                queue.add(attachDeleteRequest);
            }
        });

    }

}
