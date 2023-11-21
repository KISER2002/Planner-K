package com.example.project2.Home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project2.MyPage.UserEdit;
import com.example.project2.R;
import com.example.project2.VolleyRequest.CoverDeleteRequest;
import com.example.project2.VolleyRequest.CoverEditRequest;
import com.example.project2.VolleyRequest.LabelDeleteRequest;
import com.example.project2.VolleyRequest.UserEditRequest;
import com.example.project2.databinding.ActivityAddLabelBinding;
import com.example.project2.databinding.ActivityCoverBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CoverActivity extends AppCompatActivity {

    private AlertDialog dialog;

    ActivityCoverBinding binding;

    private String pk;
    private String coverStr = "1";

    Uri photoUri;

    private String ImagePath;
    private String ImageName;
    private String profileImg;

    private String basicPath;

    private static final int PICK_FORM_ALBUM = 1;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        upLoadServerUri = "http://43.201.55.113/UploadToServer.php";//서버컴퓨터의 ip주소

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        coverStr = intent.getStringExtra("cover");

        if(coverStr.equals("")){

        } if(coverStr.equals("1")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#7BC86C"));
        } if(coverStr.equals("2")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#F5DD29"));
        } if(coverStr.equals("3")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#FFAF3F"));
        } if(coverStr.equals("4")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#EF7564"));
        } if(coverStr.equals("5")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#CD8DE5"));
        } if(coverStr.equals("6")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#5BA4CF"));
        } if(coverStr.equals("7")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#29CCE5"));
        } if(coverStr.equals("8")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#6DECA9"));
        } if(coverStr.equals("9")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#FF8ED4"));
        } if(coverStr.equals("10")){
            binding.coverImageView.setBackgroundColor(Color.parseColor("#172B4D"));
        } if(coverStr.startsWith("/Images/")) {
            binding.coverImageView.setBackgroundColor(Color.WHITE);
            binding.coverImageView.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load("http://43.201.55.113" + coverStr).into(binding.coverImageView);
        }

        binding.color1.setOnClickListener(colorClick);
        binding.color2.setOnClickListener(colorClick);
        binding.color3.setOnClickListener(colorClick);
        binding.color4.setOnClickListener(colorClick);
        binding.color5.setOnClickListener(colorClick);
        binding.color6.setOnClickListener(colorClick);
        binding.color7.setOnClickListener(colorClick);
        binding.color8.setOnClickListener(colorClick);
        binding.color9.setOnClickListener(colorClick);
        binding.color10.setOnClickListener(colorClick);

        // 프로필 사진 설정
        binding.attachmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 관련 권한 허용
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED) {
                    } else {
                        ActivityCompat.requestPermissions(CoverActivity.this, new String[]{
                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                }

                CharSequence info[] = new CharSequence[]{"사진 앨범 선택"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CoverActivity.this);
                builder.setTitle("업로드 이미지 선택");
                builder.setItems(info, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent1, PICK_FORM_ALBUM);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        if(coverStr.equals("")) {
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            binding.backBtn.setText("삭제");
            binding.backBtn.setTextColor(Color.RED);
            binding.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CoverActivity.this);
                    builder.setTitle("삭제").setMessage("커버를 삭제하시겠습니까?")
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
                                                    Toast.makeText(getApplicationContext(), "커버가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(getApplicationContext(),"삭제에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };

                                    //서버로 volley 를 이용해서 요청을 함
                                    CoverDeleteRequest coverDeleteRequest = new CoverDeleteRequest(pk, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(CoverActivity.this);
                                    queue.add(coverDeleteRequest);
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
            });
        }

        binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (photoUri != null) {
                    String path = getPath(photoUri);
                    String name = getName(photoUri);
                    profileImg = "/Images/" + name;

                    ImagePath = path;
                    ImageName = name;
                } else {
                    profileImg = coverStr;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//Register php에 response
                            boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                            if (success) {//정보수정에 성공한 경우
                                Toast.makeText(getApplicationContext(), "정보 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                if (ImagePath != null) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                            if (ImagePath.startsWith("/storage/")) {
                                                uploadFile(ImagePath);
                                            }
                                            finish();
                                        }
                                    }).start();
                                } else {
                                    finish();
                                }
                            } else {//정보수정에 실패한 경우
                                Toast.makeText(getApplicationContext(), "커버 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                CoverEditRequest coverEditRequest = new CoverEditRequest(pk, profileImg, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CoverActivity.this);
                queue.add(coverEditRequest);
            }

        });
    }

    View.OnClickListener colorClick = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            photoUri = null;
            switch(view.getId()) {
                case R.id.color1:
                    coverStr = "1";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#7BC86C"));
                    break;

                case R.id.color2:
                    coverStr = "2";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#F5DD29"));
                    break;

                case R.id.color3:
                    coverStr = "3";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#FFAF3F"));
                    break;

                case R.id.color4:
                    coverStr = "4";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#EF7564"));
                    break;

                case R.id.color5:
                    coverStr = "5";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#CD8DE5"));
                    break;

                case R.id.color6:
                    coverStr = "6";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#5BA4CF"));
                    break;

                case R.id.color7:
                    coverStr = "7";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#29CCE5"));
                    break;
                case R.id.color8:
                    coverStr = "8";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#6DECA9"));
                    break;

                case R.id.color9:
                    coverStr = "9";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#FF8ED4"));
                    break;

                case R.id.color10:
                    coverStr = "10";
                    binding.coverImageView.setBackgroundColor(Color.parseColor("#172B4D"));
                    break;

            }
        }
    };

    // 실제 경로 찾기(프로필 이미지 설정 시)
    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // 파일명 찾기
    private String getName(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // uri 아이디 찾기
    private String getUriId(Uri uri) {
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("main onActivityResult", "onActivityResult 호출");
        if (resultCode == RESULT_OK) {

            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
            if (requestCode == PICK_FORM_ALBUM) {

                Log.v("PICK_FORM_ALBUM", "requestCode == PICK_FORM_ALBUM");
                photoUri = data.getData();
                binding.coverImageView.setBackgroundColor(Color.WHITE);
                binding.coverImageView.setImageURI(photoUri);

            }
        }
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File not exist :"
                    + ImagePath + "" + ImageName);
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + ImageName;

//                            Toast.makeText(UserEdit.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(UserEdit.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(UserEdit.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;

        } // End else block
    }
}
