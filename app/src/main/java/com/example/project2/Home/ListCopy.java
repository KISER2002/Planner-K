package com.example.project2.Home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.example.project2.VolleyRequest.ListCopyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ListCopy extends AppCompatActivity {
    private final int LIST_CODE = 101;
    private final int POSITION_CODE = 102;

    private String pk, old_idx, old_board_idx, new_idx, new_board_idx, title;
    private EditText title_tv;
    private TextView current_board, current_position, confirm_btn;
    private LinearLayout edit_board_button, edit_position_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_copy);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        old_idx = intent.getStringExtra("idx");
        old_board_idx = intent.getStringExtra("bno");
        title = intent.getStringExtra("title");
        new_idx = old_idx;
        new_board_idx = old_board_idx;

        title_tv = findViewById(R.id.title);
        current_board = findViewById(R.id.current_board);
        current_position = findViewById(R.id.current_position);
        confirm_btn = findViewById(R.id.confirm_button);
        edit_board_button = findViewById(R.id.edit_board_button);
        edit_position_button = findViewById(R.id.edit_position_btn);

        title_tv.setText(title);
        current_position.setText(old_idx);

        edit_board_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BoardListActivity.class);
                i.putExtra("idx", new_board_idx);
                resultLauncher.launch(i);
            }
        });

        edit_position_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListPositionActivity.class);
                i.putExtra("idx", new_idx);
                i.putExtra("old_board_idx", old_board_idx);
                i.putExtra("list_idx", new_board_idx);
                resultLauncher1.launch(i);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_tv.getText().toString();

                if(old_idx.equals(new_idx) && old_board_idx.equals(new_board_idx)){
                    Toast.makeText(ListCopy.this, "변경된 값이 없습니다.", Toast.LENGTH_SHORT).show();
                } else if(new_idx.equals("null")){
                    Toast.makeText(ListCopy.this, "포지션을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if(title.equals("")) {
                    Toast.makeText(ListCopy.this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
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
                                    Toast.makeText(getApplicationContext(),"리스트가 복사되었습니다.",Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    ListCopyRequest cardPosEditRequest = new ListCopyRequest(pk, new_idx, new_board_idx, title, old_idx, old_board_idx, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ListCopy.this);
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
                        new_board_idx = list_idx;
                        current_board.setText(title);
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
