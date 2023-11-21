package com.example.project2.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.project2.R;

public class Mp4Activity extends  AppCompatActivity implements View.OnClickListener {
    boolean isPrepared = false;
    boolean isTouch = false;

    VideoView vv;
    Button playBt;
    SeekBar seekBar;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4);
        vv = findViewById(R.id.vv);
        playBt = findViewById(R.id.play_bt);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        Uri uri = Uri.parse(url);
        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //재생 준비가 완료 되었을 때
                isPrepared = true;
                seekBar.setMax(vv.getDuration());
            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //재생이 끝났을 때
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vv.seekTo(seekBar.getProgress());
                isTouch =false;
            }
        });

//        vv.getDuration(); //총 재생시간을 ms 가져옴
//        vv.getCurrentPosition(); //현재 재생 부분 가져옴  ms
//        vv.seekTo(1000);  // 해당하는 재생 위치로 이동 ms
//        vv.pause(); //일시 정지
//        vv.stopPlayback(); //완전 스톱

        MediaController controller = new MediaController(this);
        vv.setMediaController(controller);

        findViewById(R.id.play_bt).setOnClickListener(this);
        findViewById(R.id.pre_btn).setOnClickListener(this);
        findViewById(R.id.next_btn).setOnClickListener(this);
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.sendEmptyMessageDelayed(0,500);
            if(!isTouch){
                seekBar.setProgress(vv.getCurrentPosition());
            }
        }
    };

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.play_bt){
            if(vv.isPlaying()){
                vv.pause();
                playBt.setText("▶");
            }else{
                vv.start();
                playBt.setText("||");
            }
        }else if(view.getId() == R.id.pre_btn){
            if(vv.getCurrentPosition() <= 10000){
                vv.seekTo(0);
            } else {
                vv.seekTo(vv.getCurrentPosition() - 10000); // 10초 뒤로 갑니다
            }
        }else if(view.getId() == R.id.next_btn){
            vv.seekTo(vv.getCurrentPosition() + 10000); // 10초 뒤로 갑니다
        }
    }
}