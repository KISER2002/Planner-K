package com.example.project2;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project2.Account.fragAccount;
import com.example.project2.Board.fragBoard;
import com.example.project2.Chat.fragChat;
import com.example.project2.Home.fragHome;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //프래그먼트 변수
    Fragment fragment_home;
    Fragment fragment_board;
    Fragment fragment_chat;
    Fragment fragment_account;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //프래그먼트 생성
        fragment_home = new fragHome();
        fragment_board = new fragBoard();
        fragment_chat = new fragChat();
        fragment_account = new fragAccount();

        // 바텀 네비게이션
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 초기 플래그먼트 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, fragment_home).commitAllowingStateLoss();
//        bottomNavigationView.setSelectedItemId(R.id.account);

        // 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.seat:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout,fragment_home).commitAllowingStateLoss();
                        return true;
                    case R.id.board:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout,fragment_board).commitAllowingStateLoss();
                        return true;
                    case R.id.chat:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout,fragment_chat).commitAllowingStateLoss();
                        return true;
                    case R.id.account:
                        getSupportFragmentManager().beginTransaction() .replace(R.id.main_layout,fragment_account).commitAllowingStateLoss();
                        return true;
                }
                return true;
            }
        });

    }
}
