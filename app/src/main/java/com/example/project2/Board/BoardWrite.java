package com.example.project2.Board;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.BoardImageAdapter;
import com.example.project2.ItemTouchHelper.ItemMoveCallback;
import com.example.project2.R;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.BoardImageRequest;
import com.example.project2.VolleyRequest.BoardRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class BoardWrite extends AppCompatActivity {

    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    int listSize = 0;

    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    BoardImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터

    TextView saveBtn, imagesCount;
    EditText titleEt, contentEt;
    ImageButton add_photo_btn;
    ImageView backBtn;
    Uri photoUri;

    private static final int PICK_FORM_ALBUM = 1;

    private String ImagePath;
    private String ImageName;
    private String boardImg;

    private String basicPath;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    SessionManager sessionManager;

    private int newBno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_write);

        Intent intent = getIntent();
        newBno = intent.getIntExtra("newBno", 0);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);

        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.board_save_btn);
        titleEt = findViewById(R.id.board_title);
        contentEt = findViewById(R.id.board_content);
        add_photo_btn = findViewById(R.id.board_add_photo_btn);
        imagesCount = findViewById(R.id.images_count);
        recyclerView = findViewById(R.id.board_imageView_rv);

        Uri NoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.empty);
        basicPath = "drawable://" + R.drawable.empty;
        photoUri = NoneUri;

        upLoadServerUri = "http://43.201.55.113/UploadToServer.php";//서버컴퓨터의 ip주소

        adapter = new BoardImageAdapter(uriList, getApplicationContext());

        ItemTouchHelper.Callback callback =
                new ItemMoveCallback((ItemMoveCallback.ItemTouchHelperContract) adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅


        adapter.setOnItemClickListener(new BoardImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                listSize = uriList.size()-1;
                imagesCount.setText(listSize + "/10 장");
            }
        });

        //사진 관련 권한 허용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(BoardWrite.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, PICK_FORM_ALBUM);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (photoUri == NoneUri) {
                    String path = "drawable://" + R.drawable.empty;
                    String name = "empty";
                    boardImg = name;

                    ImagePath = path;
                    ImageName = name;
                } else {
                    String path = getPath(photoUri);
                    String name = getName(photoUri);
                    boardImg = "/Images/" + name;

                    ImagePath = path;
                    ImageName = name;
                }


                //editText에 입력되어있는 값을 get(가져온다)해온다
                String title = titleEt.getText().toString();
                String content = contentEt.getText().toString();

                // 현재 시간 가져오기
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String getTime = simpleDate.format(mDate);

                //제목이 공백일 시
                if (title.equals("")) {
                    Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    titleEt.requestFocus(); //커서 이동

                } else if (!title.equals("")) {

                    for (int i = 0; i < uriList.size(); i++){
                        Uri imageUri = uriList.get(i);
                        try{
                            String path = getPath(imageUri);
                            String name = getName(imageUri);
                            boardImg = "/Images/" + name;

                            ImagePath = path;
                            ImageName = name;

                            int SDK_INT = android.os.Build.VERSION.SDK_INT;
                            if (SDK_INT > 8)
                            {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                        .permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                //your codes here
                                uploadFile(ImagePath);
                            }

                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jasonObject = new JSONObject(response);//Board php에 response
                                    boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                                    if (success) {//게시글 작성에 성공한 경우

                                    } else {//글 작성에 실패한 경우

                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        //서버로 volley 를 이용해서 요청을 함
                        BoardImageRequest boardImageRequest = new BoardImageRequest(String.valueOf(i+1), String.valueOf(newBno), boardImg, responseListener1);
                        RequestQueue queue1 = Volley.newRequestQueue(BoardWrite.this);
                        queue1.add(boardImageRequest);


                        } catch (Exception e){
                            Log.e("TAG", "File Select Error", e);
                         }
                    }

                    String thumbnailImage;
                    if(uriList.size() == 0){
                        thumbnailImage = "empty";
                    }else {
                        Uri thumbnailImageUri = uriList.get(0);
                        String name = getName(thumbnailImageUri);
                        thumbnailImage = "/Images/" + name;
                    }

                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jasonObject = new JSONObject(response);//Board php에 response
                                boolean success = jasonObject.getBoolean("success");//Board php에 sucess
                                if (success) {//게시글 작성에 성공한 경우
                                    Toast.makeText(getApplicationContext(), "게시글이 작성되었습니다", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {//글 작성에 실패한 경우
                                    Toast.makeText(getApplicationContext(), "게시글 작성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    //서버로 volley 를 이용해서 요청을 함
                    BoardRequest boardRequest = new BoardRequest(String.valueOf(newBno), mId, title, content, thumbnailImage, getTime, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BoardWrite.this);
                    queue.add(boardRequest);


                }
            }
        });
    }

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
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else{   // 이미지를 하나라도 선택한 경우
            if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                if(listSize == 10){
                    Toast.makeText(getApplicationContext(), "이미지는 최대 10장까지만 첨부 가능합니다.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);

//                    adapter = new BoardImageAdapter(uriList, getApplicationContext());
//                    recyclerView.setAdapter(adapter);
//
//                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//
//                    recyclerView.setLayoutManager(mLayoutManager);
                }
            }
            else{      // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                if(clipData.getItemCount() > 10 - listSize){
                    Toast.makeText(getApplicationContext(), "이미지는 최대 10장까지만 첨부 가능합니다.", Toast.LENGTH_LONG).show();
                }else if(clipData.getItemCount() > 10){   // 선택한 이미지가 11장 이상인 경우
                    Toast.makeText(getApplicationContext(), "이미지는 최대 10장까지만 선택 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                    Log.e("TAG", "multiple choice");

//                    uriList = new ArrayList<>();

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.
                        } catch (Exception e) {
                            Log.e("TAG", "File select error", e);
                        }
                    }
                }
            }
        }
        listSize = uriList.size();
        imagesCount.setText(listSize + "/10 장");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));     // 리사이클러뷰 수평 스크롤 적용
        adapter.notifyDataSetChanged();
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

//                            Toast.makeText(Join.this, "File Upload Complete.",
//                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "Got Exception : see logcat ",
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return serverResponseCode;

        } // End else block
    }
}
