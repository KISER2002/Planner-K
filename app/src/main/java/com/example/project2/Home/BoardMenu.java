package com.example.project2.Home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.SessionManager;
import com.example.project2.VolleyRequest.CoverShowRequest;
import com.example.project2.VolleyRequest.StarStatusRequest;
import com.example.project2.VolleyRequest.WorkSpaceEditRequest;
import com.example.project2.VolleyRequest.WorkSpaceRequest;
import com.example.project2.databinding.ActivityBoardMenuBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoardMenu extends Activity {
    private static final String LIST_URL = "http://43.201.55.113/LoadBoard.php";

    ActivityBoardMenuBinding binding;
    String room_idx, is_star, cover_show;
    private boolean starValidate=false;
    private boolean coverValidate=false;

    private static final int PICK_FORM_ALBUM = 1;

    Uri photoUri;

    private AlertDialog dialog;

    private String ImagePath;
    private String ImageName;
    private String roomImg;

    private String basicPath;

    String upLoadServerUri = null;
    private int serverResponseCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        room_idx = intent.getStringExtra("room_idx");

        LoadBoard(room_idx);

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

        Log.v("main onActivityResult", "onActivityResult 호출");
        if (resultCode == RESULT_OK) {

            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
            if (requestCode == PICK_FORM_ALBUM) {

                Log.v("PICK_FORM_ALBUM", "requestCode == PICK_FORM_ALBUM");
                photoUri = data.getData();
                binding.boardImg.setImageURI(photoUri);

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

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(Join.this, "MalformedURLException",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
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

    private void LoadBoard(String getBno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            List<ListItem> itemListItem = new ArrayList<>();

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                String getIdx = product.getString("idx");
                                String getTitle = product.getString("title");
                                String getWriter = product.getString("writer");
                                String getImage = product.getString("image");
                                String getStar = product.getString("star");
                                String getCover_show = product.getString("cover_show");

                                roomImg = getImage;
                                is_star = getStar;
                                cover_show = getCover_show;

                                if(is_star.equals("1")){
                                    Glide.with(BoardMenu.this).load(R.drawable.star_ok).into(binding.startIv);
                                    starValidate = true;
                                }
                                if(is_star.equals("0")){
                                    Glide.with(BoardMenu.this).load(R.drawable.star).into(binding.startIv);
                                    starValidate = false;
                                }

                                if(cover_show.equals("1")){
                                    binding.coverSwitch.setChecked(true);
                                    coverValidate = true;
                                }
                                if(cover_show.equals("0")){
                                    binding.coverSwitch.setChecked(false);
                                    coverValidate = false;
                                }

                                binding.starBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(starValidate){
                                            is_star = "0";
                                            Glide.with(BoardMenu.this).load(R.drawable.star).into(binding.startIv);
                                            starValidate=false;
                                            Toast.makeText(BoardMenu.this, "즐겨찾기가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                        } else{
                                            is_star = "1";
                                            Glide.with(BoardMenu.this).load(R.drawable.star_ok).into(binding.startIv);
                                            starValidate=true;
                                            Toast.makeText(BoardMenu.this, "즐겨찾기 설정되었습니다.", Toast.LENGTH_SHORT).show();
                                        }

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                    boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                    if (success) {//좋아요 클릭에 성공한 경우
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();

                                                    }
                                                    else{//회원가입에 실패한 경우
                                                        Toast.makeText(getApplicationContext(),"즐겨찾기에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        StarStatusRequest starStatusRequest = new StarStatusRequest(getIdx, is_star, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(BoardMenu.this);
                                        queue.add(starStatusRequest);
                                    }
                                });

                                binding.archiveBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), ArchivedItem.class);
                                        intent.putExtra("bno", getIdx);
                                        startActivity(intent);
                                    }
                                });

                                binding.coverSwitch.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(coverValidate){
                                            cover_show = "0";
                                            binding.coverSwitch.setChecked(false);
                                            coverValidate=false;

                                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                        boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                        if (success) {//좋아요 클릭에 성공한 경우
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();

//                                                            Intent intent = new Intent();
//                                                            intent.putExtra("room_idx", getIdx);
//                                                            intent.putExtra("cover_show",cover_show);
//                                                            setResult(RESULT_OK, intent); // [인텐트 종료 코드 지정]
//
//                                                            finish();
                                                        }
                                                        else{//회원가입에 실패한 경우
                                                            Toast.makeText(getApplicationContext(),"커버 설정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            CoverShowRequest coverShowRequest = new CoverShowRequest(getIdx, cover_show, responseListener1);
                                            RequestQueue queue1 = Volley.newRequestQueue(BoardMenu.this);
                                            queue1.add(coverShowRequest);

                                        } else{
                                            cover_show = "1";
                                            binding.coverSwitch.setChecked(true);
                                            coverValidate=true;

                                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                        boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                        if (success) {//좋아요 클릭에 성공한 경우
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();

//                                                            Intent intent = new Intent();
//                                                            intent.putExtra("room_idx", getIdx);
//                                                            intent.putExtra("cover_show",cover_show);
//                                                            setResult(RESULT_OK, intent); // [인텐트 종료 코드 지정]
//
//                                                            finish();
                                                        }
                                                        else{//회원가입에 실패한 경우
                                                            Toast.makeText(getApplicationContext(),"커버 설정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            CoverShowRequest coverShowRequest = new CoverShowRequest(getIdx, cover_show, responseListener1);
                                            RequestQueue queue1 = Volley.newRequestQueue(BoardMenu.this);
                                            queue1.add(coverShowRequest);

                                        }
                                    }
                                });

                                binding.copyBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), CopyBoard.class);
                                        intent.putExtra("room_idx", getIdx);
                                        intent.putExtra("image", getImage);
                                        startActivity(intent);
                                    }
                                });

                                binding.title.setText(getTitle);
                                if(getImage.equals("basic_image")){
                                    Glide.with(BoardMenu.this).load(R.drawable.null_room).into(binding.boardImg);
                                } else{
                                    Glide.with(BoardMenu.this).load("http://43.201.55.113" + getImage).override(130,130).into(binding.boardImg);
                                }

                                Uri basicUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.null_room);
                                basicPath = "drawable://" + R.drawable.null_room;
                                photoUri = basicUri;

                                upLoadServerUri = "http://43.201.55.113/UploadToServer.php";//서버컴퓨터의 ip주소

                                //프로필 사진 관련 권한 허용
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                                            PackageManager.PERMISSION_GRANTED &&
                                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                                    PackageManager.PERMISSION_GRANTED) {
                                    } else {
                                        ActivityCompat.requestPermissions(BoardMenu.this, new String[]{
                                                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                    }
                                }

                                // 채팅방 사진 설정
                                binding.imageBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CharSequence info[] = new CharSequence[]{"사진 앨범 선택", "기본 이미지로 변경"};


                                        AlertDialog.Builder builder = new AlertDialog.Builder(BoardMenu.this, R.style.Base_Theme_AppCompat_Light_Dialog);
                                        builder.setTitle("업로드 이미지 선택");
                                        builder.setItems(info, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:

                                                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                                                        intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                                        intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                        startActivityForResult(intent1, PICK_FORM_ALBUM);
                                                        break;
                                                    case 1:
                                                        // 기본 이미지로 변경
                                                        photoUri = basicUri;
                                                        binding.boardImg.setImageURI(photoUri);
                                                        Toast.makeText(getApplicationContext(), "기본 이미지로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                });

                                binding.confirmBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (photoUri == basicUri) {
                                            String path = "drawable://" + R.drawable.profile_img;
                                            String name = "basic_image";
                                            roomImg = name;

                                            ImagePath = path;
                                            ImageName = name;
                                        } else {
                                            String path = getPath(photoUri);
                                            String name = getName(photoUri);
                                            roomImg = "/Images/" + name;

                                            ImagePath = path;
                                            ImageName = name;
                                        }


                                        //editText에 입력되어있는 값을 get(가져온다)해온다
                                        String workSpaceName = binding.title.getText().toString();

                                        //닉네임이 공백일 시
                                        if (workSpaceName.equals("")) {
                                            Toast.makeText(getApplicationContext(), "보드 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                            binding.title.requestFocus(); //커서 이동
                                            //키보드 보이게 하는 부분
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                                        } else {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                        boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                        if (success) {//회원가입에 성공한 경우
                                                            if (ImagePath != null) {
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                            }
                                                                        });
                                                                        if (ImagePath == basicPath) {

                                                                        } else {
                                                                            uploadFile(ImagePath);
                                                                        }
                                                                    }
                                                                }).start();
                                                            } else {
                                                            }
                                                        } else {//회원가입에 실패한 경우
                                                            Toast.makeText(getApplicationContext(), "보드 생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            WorkSpaceEditRequest workSpaceRequest = new WorkSpaceEditRequest(getIdx, workSpaceName, roomImg, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(BoardMenu.this);
                                            queue.add(workSpaceRequest);
                                        }
                                    }
                                });


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BoardMenu.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(BoardMenu.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idx", getBno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(BoardMenu.this);
        requestQueue.add(stringRequest);
    }
}