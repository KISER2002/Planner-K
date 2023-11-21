package com.example.project2.Forget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;

public class ForgetActivity extends AppCompatActivity {
    public static Activity forgetActivity;

    Button IdBtn, PwBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        forgetActivity = ForgetActivity.this;

        IdBtn = findViewById(R.id.find_id_btn);
        PwBtn = findViewById(R.id.find_pw_btn);

        IdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetId.class);
                startActivity(intent);
            }
        });

        PwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgetPw.class);
                startActivity(intent);
            }
        });

    }
}
