package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project2.R;

public class ImageActivity extends AppCompatActivity {
    ImageView iv;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        iv = findViewById(R.id.image);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        Glide.with(ImageActivity.this).load(url).into(iv);
    }
}
