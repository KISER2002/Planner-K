package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.project2.VolleyRequest.MoveAllCardRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MoveAllCard extends AppCompatActivity {
    private final int LIST_CODE = 101;
    private final int POSITION_CODE = 102;

    private String pk, old_idx, old_board_idx, new_idx, new_board_idx;
    private TextView current_board, current_list, confirm_btn;
    private LinearLayout edit_board_button, edit_list_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.move_all_card);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        old_idx = intent.getStringExtra("idx");
        old_board_idx = intent.getStringExtra("bno");
        new_idx = old_idx;
        new_board_idx = old_board_idx;

        current_board = findViewById(R.id.current_board);
        current_list = findViewById(R.id.current_list);
        confirm_btn = findViewById(R.id.confirm_button);
        edit_board_button = findViewById(R.id.edit_board_button);
        edit_list_button = findViewById(R.id.edit_list_btn);

        edit_board_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BoardListActivity.class);
                i.putExtra("idx", new_board_idx);
                resultLauncher.launch(i);
            }
        });

        edit_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditListActivity.class);
                i.putExtra("idx", new_idx);
                i.putExtra("bno", new_board_idx);
                resultLauncher1.launch(i);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Toast.makeText(getApplicationContext(),"카드가 이동되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "이동에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                MoveAllCardRequest moveAllCardRequest = new MoveAllCardRequest(pk, new_board_idx, new_idx, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MoveAllCard.this);
                queue.add(moveAllCardRequest);

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
                        new_board_idx = list_idx;
                        current_board.setText(title);
                        current_list.setText("리스트를 선택해주세요");
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
                        String title = intent.getStringExtra("title");
                        String list_idx = intent.getStringExtra("list_idx");
                        new_idx = list_idx;
                        current_list.setText(title);
                    }
                }
            });
}
