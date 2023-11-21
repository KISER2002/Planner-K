package com.example.project2.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;

public class ArchivedItem extends AppCompatActivity {

    private String bno;
    private LinearLayout archive_list_btn, archive_card_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_item);

        Intent intent = getIntent();
        bno = intent.getStringExtra("bno");

        archive_list_btn = findViewById(R.id.archive_list);
        archive_card_btn = findViewById(R.id.archive_card);

        archive_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ArchiveListList.class);
                i.putExtra("bno", bno);
                startActivity(i);
            }
        });

        archive_card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ArchiveCardList.class);
                i.putExtra("bno", bno);
                startActivity(i);
            }
        });
    }
}
