package com.example.project2.Home;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.R;
import com.example.project2.VolleyRequest.LabelRequest;
import com.example.project2.databinding.ActivityAddLabelBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class AddLabelActivity extends AppCompatActivity {

    ActivityAddLabelBinding binding;

    private String selectedColorStr = "no_color";
    private String selectedColorStr_kor = "색상 없음";
    private int selectedColorInt = -9342607;
    private int colorIdx = 31;
    String bno;

    String title = "";

    ImageView iv;
    TextView tv;
    Drawable background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddLabelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        bno = intent.getStringExtra("bno");

        binding.lightGreen.setOnClickListener(colorClick);
        binding.green.setOnClickListener(colorClick);
        binding.darkGreen.setOnClickListener(colorClick);

        binding.lightYellow.setOnClickListener(colorClick);
        binding.yellow.setOnClickListener(colorClick);
        binding.darkYellow.setOnClickListener(colorClick);

        binding.lightOrange.setOnClickListener(colorClick);
        binding.orange.setOnClickListener(colorClick);
        binding.darkOrange.setOnClickListener(colorClick);

        binding.lightRed.setOnClickListener(colorClick);
        binding.red.setOnClickListener(colorClick);
        binding.darkRed.setOnClickListener(colorClick);

        binding.lightPurple.setOnClickListener(colorClick);
        binding.purple.setOnClickListener(colorClick);
        binding.darkPurple.setOnClickListener(colorClick);

        binding.lightBlue.setOnClickListener(colorClick);
        binding.blue.setOnClickListener(colorClick);
        binding.darkBlue.setOnClickListener(colorClick);

        binding.lightSky.setOnClickListener(colorClick);
        binding.sky.setOnClickListener(colorClick);
        binding.darkSky.setOnClickListener(colorClick);

        binding.lightLime.setOnClickListener(colorClick);
        binding.lime.setOnClickListener(colorClick);
        binding.darkLime.setOnClickListener(colorClick);

        binding.lightPink.setOnClickListener(colorClick);
        binding.pink.setOnClickListener(colorClick);
        binding.darkPink.setOnClickListener(colorClick);

        binding.lightBlack.setOnClickListener(colorClick);
        binding.black.setOnClickListener(colorClick);
        binding.darkBlack.setOnClickListener(colorClick);

        binding.noColor.setOnClickListener(colorClick);


        binding.addBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.addLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장 버튼 클릭 시 실행 코드
                title = binding.titleEt.getText().toString();

                if(title.equals("") && selectedColorStr.equals("no_color")){
                    Toast.makeText(getApplicationContext(), "[색상 없음]이 선택되었기 때문에,\n반드시 이름을 입력해야만 합니다.", Toast.LENGTH_SHORT).show();
                } else {
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
                                    finish();
                                }
                                else{//저장에 실패한 경우
                                    Toast.makeText(getApplicationContext(),"생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    LabelRequest labelRequest = new LabelRequest(String.valueOf(colorIdx), bno, title, selectedColorStr_kor, selectedColorStr, String.valueOf(selectedColorInt), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(AddLabelActivity.this);
                    queue.add(labelRequest);
                }
            }
        });
    }

    View.OnClickListener colorClick = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.light_green:
                    selectedColorStr = "light_green";
                    selectedColorStr_kor = "연한 녹색";
                    colorIdx = 1;
                    iv = (ImageView) binding.lightGreen.getChildAt(0);
                    tv = (TextView) binding.lightGreen.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.green:
                    selectedColorStr = "green";
                    selectedColorStr_kor = "녹색";
                    colorIdx = 2;
                    iv = (ImageView) binding.green.getChildAt(0);
                    tv = (TextView) binding.green.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_green:
                    selectedColorStr = "dark_green";
                    selectedColorStr_kor = "진한 녹색";
                    colorIdx = 3;
                    iv = (ImageView) binding.darkGreen.getChildAt(0);
                    tv = (TextView) binding.darkGreen.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_yellow:
                    selectedColorStr = "light_yellow";
                    selectedColorStr_kor = "연한 노랑";
                    colorIdx = 4;
                    iv = (ImageView) binding.lightYellow.getChildAt(0);
                    tv = (TextView) binding.lightYellow.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.yellow:
                    selectedColorStr = "yellow";
                    selectedColorStr_kor = "노랑";
                    colorIdx = 5;
                    iv = (ImageView) binding.yellow.getChildAt(0);
                    tv = (TextView) binding.yellow.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_yellow:
                    selectedColorStr = "dark_yellow";
                    selectedColorStr_kor = "진한 노랑";
                    colorIdx = 6;
                    iv = (ImageView) binding.darkYellow.getChildAt(0);
                    tv = (TextView) binding.darkYellow.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_orange:
                    selectedColorStr = "light_orange";
                    selectedColorStr_kor = "연한 주황";
                    colorIdx = 7;
                    iv = (ImageView) binding.lightOrange.getChildAt(0);
                    tv = (TextView) binding.lightOrange.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.orange:
                    selectedColorStr = "orange";
                    selectedColorStr_kor = "주황";
                    colorIdx = 8;
                    iv = (ImageView) binding.orange.getChildAt(0);
                    tv = (TextView) binding.orange.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_orange:
                    selectedColorStr = "dark_orange";
                    selectedColorStr_kor = "진한 주황";
                    colorIdx = 9;
                    iv = (ImageView) binding.darkOrange.getChildAt(0);
                    tv = (TextView) binding.darkOrange.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_red:
                    selectedColorStr = "light_red";
                    selectedColorStr_kor = "연한 빨강";
                    colorIdx = 10;
                    iv = (ImageView) binding.lightRed.getChildAt(0);
                    tv = (TextView) binding.lightRed.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.red:
                    selectedColorStr = "red";
                    selectedColorStr_kor = "빨강";
                    colorIdx = 11;
                    iv = (ImageView) binding.red.getChildAt(0);
                    tv = (TextView) binding.red.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_red:
                    selectedColorStr = "dark_red";
                    selectedColorStr_kor = "진한 빨강";
                    colorIdx = 12;
                    iv = (ImageView) binding.darkRed.getChildAt(0);
                    tv = (TextView) binding.darkRed.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_purple:
                    selectedColorStr = "light_purple";
                    selectedColorStr_kor = "연한 보라";
                    colorIdx = 13;
                    iv = (ImageView) binding.lightPurple.getChildAt(0);
                    tv = (TextView) binding.lightPurple.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.purple:
                    selectedColorStr = "purple";
                    selectedColorStr_kor = "보라";
                    colorIdx = 14;
                    iv = (ImageView) binding.purple.getChildAt(0);
                    tv = (TextView) binding.purple.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_purple:
                    selectedColorStr = "dark_purple";
                    selectedColorStr_kor = "진한 보라";
                    colorIdx = 15;
                    iv = (ImageView) binding.darkPurple.getChildAt(0);
                    tv = (TextView) binding.darkPurple.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_blue:
                    selectedColorStr = "light_blue";
                    selectedColorStr_kor = "연한 파랑";
                    colorIdx = 16;
                    iv = (ImageView) binding.lightBlue.getChildAt(0);
                    tv = (TextView) binding.lightBlue.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.blue:
                    selectedColorStr = "blue";
                    selectedColorStr_kor = "파랑";
                    colorIdx = 17;
                    iv = (ImageView) binding.blue.getChildAt(0);
                    tv = (TextView) binding.blue.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_blue:
                    selectedColorStr = "dark_blue";
                    selectedColorStr_kor = "진한 파랑";
                    colorIdx = 18;
                    iv = (ImageView) binding.darkBlue.getChildAt(0);
                    tv = (TextView) binding.darkBlue.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_sky:
                    selectedColorStr = "light_sky";
                    selectedColorStr_kor = "연한 하늘";
                    colorIdx = 19;
                    iv = (ImageView) binding.lightSky.getChildAt(0);
                    tv = (TextView) binding.lightSky.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.sky:
                    selectedColorStr = "sky";
                    selectedColorStr_kor = "하늘";
                    colorIdx = 20;
                    iv = (ImageView) binding.sky.getChildAt(0);
                    tv = (TextView) binding.sky.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_sky:
                    selectedColorStr = "dark_sky";
                    selectedColorStr_kor = "진한 하늘";
                    colorIdx = 21;
                    iv = (ImageView) binding.darkSky.getChildAt(0);
                    tv = (TextView) binding.darkSky.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_lime:
                    selectedColorStr = "light_lime";
                    selectedColorStr_kor = "연한 라임";
                    colorIdx = 22;
                    iv = (ImageView) binding.lightLime.getChildAt(0);
                    tv = (TextView) binding.lightLime.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.lime:
                    selectedColorStr = "lime";
                    selectedColorStr_kor = "라임";
                    colorIdx = 23;
                    iv = (ImageView) binding.lime.getChildAt(0);
                    tv = (TextView) binding.lime.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_lime:
                    selectedColorStr = "dark_lime";
                    selectedColorStr_kor = "진한 라임";
                    colorIdx = 24;
                    iv = (ImageView) binding.darkLime.getChildAt(0);
                    tv = (TextView) binding.darkLime.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_pink:
                    selectedColorStr = "light_pink";
                    selectedColorStr_kor = "연한 분홍";
                    colorIdx = 25;
                    iv = (ImageView) binding.lightPink.getChildAt(0);
                    tv = (TextView) binding.lightPink.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.pink:
                    selectedColorStr = "pink";
                    selectedColorStr_kor = "분홍";
                    colorIdx = 26;
                    iv = (ImageView) binding.pink.getChildAt(0);
                    tv = (TextView) binding.pink.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_pink:
                    selectedColorStr = "dark_pink";
                    selectedColorStr_kor = "진한 분홍";
                    colorIdx = 27;
                    iv = (ImageView) binding.darkPink.getChildAt(0);
                    tv = (TextView) binding.darkPink.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.light_black:
                    selectedColorStr = "light_black";
                    selectedColorStr_kor = "연한 검정";
                    colorIdx = 28;
                    iv = (ImageView) binding.lightBlack.getChildAt(0);
                    tv = (TextView) binding.lightBlack.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.black:
                    selectedColorStr = "black";
                    selectedColorStr_kor = "검정";
                    colorIdx = 29;
                    iv = (ImageView) binding.black.getChildAt(0);
                    tv = (TextView) binding.black.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.dark_black:
                    selectedColorStr = "dark_black";
                    selectedColorStr_kor = "진한 검정";
                    colorIdx = 30;
                    iv = (ImageView) binding.darkBlack.getChildAt(0);
                    tv = (TextView) binding.darkBlack.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;

                case R.id.no_color:
                    selectedColorStr = "no_color";
                    selectedColorStr_kor = "색상 없음";
                    colorIdx = 31;
                    iv = (ImageView) binding.noColor.getChildAt(0);
                    tv = (TextView) binding.noColor.getChildAt(1);
                    binding.colorText.setText(tv.getText().toString());
                    background = iv.getBackground();
                    if(background instanceof ColorDrawable){
                        int bc = (((ColorDrawable) background).getColor());
                        binding.colorText.setTextColor(bc);
                        selectedColorInt = bc;
                    }
                    break;
            }
        }
    };
}
