package com.example.project2.Home;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.VolleyRequest.CardDateDeleteRequest;
import com.example.project2.VolleyRequest.CardDateEditRequest;
import com.example.project2.databinding.ActivityCalendarBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {
    ActivityCalendarBinding binding;
    String pk, start_or_end, getTime;
    Calendar calendar;

    String timeStr;
    String timeStr2;

    String calendarPickTimeStr;
    String clockPickTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        start_or_end = intent.getStringExtra("start_or_end");
        getTime = intent.getStringExtra("time");

        if(start_or_end.equals("start")){
            binding.titleTv.setText("시작 시간 설정");
        } if(start_or_end.equals("end")) {
            binding.titleTv.setText("종료 시간 설정");
        }

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 M월 dd일 HH시 mm분");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy년 M월 dd일");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH시 mm분");
        SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("HH:mm");
        Date date = new Date();

        if(getTime.equals("null")) {
            calendarPickTimeStr = simpleDateFormat2.format(date);//현재 달력 시간 값
            clockPickTimeStr = simpleDateFormat3.format(date);// 현재 시계 시간 값
            timeStr = simpleDateFormat.format(date);
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            String[] getTimeArr = getTime.split("일 ");
            calendarPickTimeStr = getTimeArr[0] + "일 ";
            clockPickTimeStr = getTimeArr[1];
            timeStr = getTime;
            binding.backBtn.setText("삭제");
            binding.backBtn.setTextColor(Color.RED);
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);
                    builder.setTitle("삭제").setMessage("삭제하시겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                if (success) {//정보수정에 성공한 경우
                                                    new Thread(new Runnable() {
                                                        public void run() {
                                                            runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                }
                                                            });
                                                        }
                                                    }).start();
                                                    finish();
                                                }
                                                else{//정보수정에 실패한 경우
                                                    Toast.makeText(CalendarActivity.this,"변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    //서버로 volley 를 이용해서 요청을 함
                                    CardDateDeleteRequest cardDateEditRequest = new CardDateDeleteRequest(pk, start_or_end, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(CalendarActivity.this);
                                    queue.add(cardDateEditRequest);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            });
        }

        timeStr2 = simpleDateFormat4.format(date); // 시계 시간 값

        binding.timeTv.setText(timeStr);
        binding.timeBtn.setText(timeStr2);

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                int finalMonth = month + 1;
                calendarPickTimeStr = year + "년 " + finalMonth + "월 " + dayOfMonth + "일 ";
                timeStr = calendarPickTimeStr + clockPickTimeStr;
                binding.timeTv.setText(timeStr);
            }
        });

        //타임 피커
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                clockPickTimeStr = hourOfDay + "시 " + minute + "분";
                timeStr = calendarPickTimeStr + clockPickTimeStr;
                binding.timeTv.setText(timeStr);
                binding.timeBtn.setText(hourOfDay + ":" + minute);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(CalendarActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, listener, 13, 20, false);

        binding.timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();
            }
        });

        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                            if (success) {//정보수정에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();
                            }
                            else{//정보수정에 실패한 경우
                                Toast.makeText(CalendarActivity.this,"변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                CardDateEditRequest cardDateEditRequest = new CardDateEditRequest(pk, timeStr, start_or_end, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CalendarActivity.this);
                queue.add(cardDateEditRequest);
            }
        });

    }
}