package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.R;
import com.example.project2.VolleyRequest.CardPosEditRequest;
import com.example.project2.VolleyRequest.CardPosEditRequest2;

import org.json.JSONException;
import org.json.JSONObject;

public class CardPosEdit extends AppCompatActivity {
    private final int LIST_CODE = 101;
    private final int POSITION_CODE = 102;

    private String pk, old_idx, old_list_idx, new_idx, new_list_idx, bno, title;
    private TextView current_column, current_position, confirm_btn;
    private LinearLayout edit_list_button, edit_position_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_pos_edit);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        old_idx = intent.getStringExtra("idx");
        old_list_idx = intent.getStringExtra("list_idx");
        bno = intent.getStringExtra("bno");
        title = intent.getStringExtra("list_title");
        new_idx = old_idx;
        new_list_idx = old_list_idx;

        current_column = findViewById(R.id.current_column);
        current_position = findViewById(R.id.current_position);
        confirm_btn = findViewById(R.id.confirm_button);
        edit_list_button = findViewById(R.id.edit_list_button);
        edit_position_button = findViewById(R.id.edit_position_btn);

        current_column.setText(title);
        current_position.setText(old_idx);

        edit_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditListActivity.class);
                i.putExtra("idx", new_list_idx);
                i.putExtra("bno", bno);
                resultLauncher.launch(i);
            }
        });

        edit_position_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditPositionActivity.class);
                i.putExtra("idx", new_idx);
                i.putExtra("old_list_idx", old_list_idx);
                i.putExtra("list_idx", new_list_idx);
                resultLauncher1.launch(i);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(old_idx.equals(new_idx) && old_list_idx.equals(new_list_idx)){
                    Toast.makeText(CardPosEdit.this, "변경된 값이 없습니다.", Toast.LENGTH_SHORT).show();
                } else if(new_idx.equals("null")){
                    Toast.makeText(CardPosEdit.this, "포지션을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if(old_list_idx.equals(new_list_idx)){ //TODO 같은 리스트 내에서의 포지션 변경일 경우
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//php에 response
                                boolean success = jasonObject.getBoolean("success");//php에 sucess
                                if (success) {//저장에 성공한 경우
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                    Toast.makeText(getApplicationContext(),"카드 위치가 변경되었습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    CardPosEditRequest2 cardPosEditRequest = new CardPosEditRequest2(old_idx, new_idx, new_list_idx, pk, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(CardPosEdit.this);
                    queue.add(cardPosEditRequest);

                } else { //TODO 서로 다른 리스트 간의 카드 변경일 경우
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
                                    Toast.makeText(getApplicationContext(),"카드 위치가 변경되었습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else {//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"카드 이동에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    CardPosEditRequest cardPosEditRequest = new CardPosEditRequest(old_idx, old_list_idx, new_idx, new_list_idx, pk, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(CardPosEdit.this);
                    queue.add(cardPosEditRequest);
                }
            }
        });
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == RESULT_OK){

                        Intent intent = result.getData();
                        String title = intent.getStringExtra("title");
                        String list_idx = intent.getStringExtra("list_idx");
                        new_list_idx = list_idx;
                        current_column.setText(title);
                        new_idx = "null";
                        current_position.setText("포지션을 선택해주세요");
                    }
                }
            });

    ActivityResultLauncher<Intent> resultLauncher1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == RESULT_OK){

                        Intent intent = result.getData();
                        String idx = intent.getStringExtra("idx");
                        new_idx = idx;
                        current_position.setText(new_idx);
                    }
                }
            });
}
