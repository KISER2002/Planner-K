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
import com.example.project2.R;
import com.example.project2.VolleyRequest.ArchiveAllCardRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class ArchiveAllCardDialog extends Activity {

    Button noBtn, yesBtn;
    String pk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.archive_all_card_dialog);
        noBtn = findViewById(R.id.noBtn);
        yesBtn = findViewById(R.id.yesBtn);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");

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
                                Toast.makeText(ArchiveAllCardDialog.this, "보관되었습니다.", Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    public void run() {
                                        ArchiveAllCardDialog.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();
                            }
                            else{
                                Toast.makeText(ArchiveAllCardDialog.this,"보관에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ArchiveAllCardRequest archiveAllCardRequest = new ArchiveAllCardRequest(pk, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ArchiveAllCardDialog.this);
                queue.add(archiveAllCardRequest);
            }
        });

    }

}
