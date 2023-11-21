package com.example.project2.Forget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;

public class FindId extends AppCompatActivity {
    private TextView userNameTv, userIdTv;
    private Button confirmBtn;

    private String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        userNameTv = findViewById(R.id.user_name_tv);
        userIdTv = findViewById(R.id.user_id_tv);
        confirmBtn = findViewById(R.id.confirm_button);

        Intent intent = getIntent();
        id = intent.getStringExtra("userID");
        name = intent.getStringExtra("userName");

        userIdTv.setText(id);
        userNameTv.setText(name);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
