package com.example.project2.Home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.project2.A.Test;
import com.example.project2.Adapter.CheckListAdapter;
import com.example.project2.Adapter.OnItemClick;
import com.example.project2.ItemTouchHelper.CheckListItemMoveCallback;
import com.example.project2.R;
import com.example.project2.VolleyRequest.ArchiveCardRequest;
import com.example.project2.VolleyRequest.CardAttachRequest;
import com.example.project2.VolleyRequest.CardCommentRequest;
import com.example.project2.VolleyRequest.CardDeleteRequest;
import com.example.project2.VolleyRequest.CardTitleEditRequest;
import com.example.project2.VolleyRequest.CardViewEditRequest;
import com.example.project2.VolleyRequest.CardViewListEditRequest;
import com.example.project2.VolleyRequest.CheckStatusRequest;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CardView extends AppCompatActivity implements OnItemClick {
    private static String URL_LOADING = "http://43.201.55.113/CardViewRead.php";
    private static String LABEL_LOADING = "http://43.201.55.113/CardLabelList.php";
    private static String ATTACH_LOADING = "http://43.201.55.113/CardAttachList.php";
    private static String COMMENT_LOADING = "http://43.201.55.113/CardCommentList.php";

    private static final int PICK_FOR_FILE = 0;
    private static final int PICK_FORM_ALBUM = 1;

    String upLoadServerUri = "http://43.201.55.113/UploadToServer.php";//서버컴퓨터의 ip주소
    private int serverResponseCode = 0;

    private String ImagePath;
    private String ImageName;
    private String profileImg;

    private AlertDialog dialog;
    Button cover_btn, edit_button;
    TextView first_title, title_tv, memo_tv, start_btn, end_btn, label_btn, attach_btn, progress_tv, comment_btn;
    ProgressBar progressBar;
    EditText memo_et, add_list_et, comment_et;
    ImageButton add_list_btn;
    ImageView back_btn, menu_btn, cover_iv;
    LinearLayout add_list_layout, end_btn_layout;
    CheckBox timeCheckBox;
    RecyclerView add_list_rv, label_rv, attach_rv, comment_rv;
    private CheckListAdapter adapter;

    private String cover, dateChecked, start_time, end_time, title, pk, idx, list_idx, bno, list_title;
    private boolean edit_mode = false;
    private int checkProgress = 0;

    private ArrayList<Card> cardList;
    private ArrayList<CardCheckList> cardCheckList;
    private ArrayList<Label> labelList;
    private ArrayList<Attachment> attachList;
    private ArrayList<CardComment> commentList;

    private final String TAG=this.getClass().getSimpleName();

    String time = get_now_time();
    // 다운받은 파일이 저장될 위치 설정
//    private final String outputFilePath = Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_DOWNLOADS + "/P2Down") + "/";
    private DownloadManager mDownloadManager;
    private Long mDownloadQueueId;
    private File file_a, dir_a;
    private String saveFolderName = "P2Down";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();

        labelList = new ArrayList<>();
        attachList = new ArrayList<>();
        commentList = new ArrayList<>();

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");

        loadLabel(pk);
        loadAttach(pk);
        loadComment(pk);
        loadList(pk);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // 브로드캐스트 리시버 등록
        // ACTION_DOWNLOAD_COMPLETE : 다운로드가 완료되었을 때 전달
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadCompleteReceiver, completeFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");

        unregisterReceiver(downloadCompleteReceiver);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");

        if(edit_mode){

        } else {
            String cardJsonArray = null;

            try {
                JSONArray jArray = new JSONArray();//배열이 필요할때
                for (int i = 0; i < cardCheckList.size(); i++) {
                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                    sObject.put("idx", cardCheckList.get(i).getCard_idx());
                    sObject.put("is_check", cardCheckList.get(i).getIsCheck());
                    sObject.put("is_edit", cardCheckList.get(i).getIsEdit());
                    sObject.put("content", cardCheckList.get(i).getContent());
                    jArray.put(sObject);
                }

//                    Log.d("JSON Test", jArray.toString());
                cardJsonArray = jArray.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                        } else {//저장에 실패한 경우
                            Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            //서버로 volley 를 이용해서 요청을 함
            CardViewListEditRequest cardViewEditRequest = new CardViewListEditRequest(pk, cardJsonArray, responseListener);
            RequestQueue queue = Volley.newRequestQueue(CardView.this);
            queue.add(cardViewEditRequest);
        }
    }

    private void URLDownloading(Uri url, String name) {
        String outputFilePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS + "/" + name) + "/";
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) CardView.this.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        File outputFile = new File(outputFilePath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        Uri downloadUri = url;
        // DownloadManager.Request을 설정하여 DownloadManager Queue에 등록하게 되면 큐에 들어간 순서대로 다운로드가 처리된다.
        // DownloadManager.Request : Request 객체를 생성하며 인자로 다운로드할 파일의 URI를 전달한다.
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        List<String> pathSegmentList = downloadUri.getPathSegments();
        // setTitle : notification 제목
        request.setTitle(name);
        request.setDescription("Downloading Dev Summit"); //setDescription: 노티피케이션에 보이는 디스크립션입니다.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);   // setNotificationVisibility : VISIBILITY_VISIBLE로 설정되면 notification에 보여진다.
        request.setDestinationUri(Uri.fromFile(outputFile));   // setDestinationUri : 파일이 저장될 위치의 URI
        request.setAllowedOverMetered(true);

        // DownloadManager 객체 생성하여 다운로드 대기열에 URI 객체를 넣는다.
        mDownloadQueueId = mDownloadManager.enqueue(request);
    }

    // 다운로드 상태조회
    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(mDownloadQueueId == reference){
                DownloadManager.Query query = new DownloadManager.Query();  // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference);
                Cursor cursor = mDownloadManager.query(query);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                int status = cursor.getInt(columnIndex);
                int reason = cursor.getInt(columnReason);

                cursor.close();

                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL :
                        Toast.makeText(CardView.this, "다운로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_PAUSED :
                        Toast.makeText(CardView.this, "다운로드가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_FAILED :
                        Toast.makeText(CardView.this, "다운로드가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };


    // 갤러리 갱신
    private void galleryAddPic(String Image_Path) {
        // 이전 사용 방식
        /*Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(Image_Path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.context.sendBroadcast(mediaScanIntent);*/

        File file = new File(Image_Path);
        MediaScannerConnection.scanFile(
                this,
                new String[]{file.toString()},
                null, null);
    }

    private String get_now_time() {
        long now = System.currentTimeMillis(); // 현재 시간을 가져온다.
        Date mDate = new Date(now); // Date형식으로 고친다.
        // 날짜, 시간을 가져오고 싶은 형태로 가져올 수 있다.
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss", Locale.KOREA);
        String now_time = simpleDate.format(mDate);
        Log.d(TAG, "태그 현재시간 now_time : " + now_time);
        return now_time;
    }

    @Override
    public void onClick(ArrayList<CardCheckList> value) {
        cardCheckList = value;
    }

    private void loadLabel(String getPk) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LABEL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                String pk = item.getString("label_pk");
                                int idx = item.getInt("idx");
                                String title = item.getString("title");
                                String color_str_kor = item.getString("color_str_kor");
                                String color_str = item.getString("color_str");
                                int color_int = item.getInt("color_int");

                                Label label = new Label();

                                label.setPk(pk);
                                label.setIdx(idx);
                                label.setTitle(title);
                                label.setColorStrKor(color_str_kor);
                                label.setColorStr(color_str);
                                label.setColorInt(color_int);

                                labelList.add(label);
                            }

                            label_rv = findViewById(R.id.label_rv);
                            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(CardView.this);
                            label_rv.setLayoutManager(flexboxLayoutManager);

                            RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerViewAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                @NonNull
                                @Override
                                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View itemView = LayoutInflater.from(CardView.this).inflate(R.layout.card_label_item, parent, false);
                                    return new RecyclerView.ViewHolder(itemView) {
                                    };
                                }

                                @Override
                                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                                    TextView title_tv = holder.itemView.findViewById(R.id.title_tv);
                                    LinearLayout label_item = holder.itemView.findViewById(R.id.label_item);
                                    title_tv.setText(labelList.get(position).getTitle());
                                    label_item.setBackgroundColor(labelList.get(position).getColorInt());

                                    if(labelList.get(position).getColorInt() == -3190467 || labelList.get(position).getColorInt() == -5739327
                                            || labelList.get(position).getColorInt() == -16618841 || labelList.get(position).getColorInt() == -11509895
                                            || labelList.get(position).getColorInt() == -13351581 || labelList.get(position).getColorInt() == -16179646
                                            || labelList.get(position).getColorInt() == -9342607){
                                        title_tv.setTextColor(Color.WHITE);
                                    } else {
                                        title_tv.setTextColor(Color.BLACK);
                                    }
                                }

                                @Override
                                public int getItemCount() {
                                    return labelList.size();
                                }
                            };
                            label_rv.setAdapter(recyclerViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CardView.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CardView.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
        @Nullable
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("pk", getPk);
            return params;
        }
    };

    RequestQueue requestQueue = Volley.newRequestQueue(CardView.this);
    requestQueue.add(stringRequest);
}

    private void loadAttach(String getPk) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ATTACH_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                String pk = item.getString("pk");
                                String bno = item.getString("bno");
                                String name = item.getString("name");
                                String type = item.getString("type");
                                String date = item.getString("date");

                                Attachment attachment = new Attachment();

                                attachment.setPk(pk);
                                attachment.setBno(bno);
                                attachment.setName(name);
                                attachment.setType(type);
                                attachment.setDate(date);

                                attachList.add(attachment);
                            }

                            attach_rv = findViewById(R.id.attach_rv);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CardView.this);
                            attach_rv.setLayoutManager(linearLayoutManager);

                            RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerViewAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                @NonNull
                                @Override
                                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View itemView = LayoutInflater.from(CardView.this).inflate(R.layout.card_attach_item, parent, false);
                                    return new RecyclerView.ViewHolder(itemView) {
                                    };
                                }

                                @Override
                                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                                    TextView name_tv = holder.itemView.findViewById(R.id.name);
                                    TextView date_tv = holder.itemView.findViewById(R.id.date);
                                    ImageView iv = holder.itemView.findViewById(R.id.image);
                                    ImageView menu = holder.itemView.findViewById(R.id.menu_btn);
                                    LinearLayout attach_item = holder.itemView.findViewById(R.id.attach_item);
                                    String type = attachList.get(position).getType();
                                    menu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {

                                            final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                                            popupMenu.getMenuInflater().inflate(R.menu.popup2,popupMenu.getMenu());
                                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem menuItem) {
                                                    if (menuItem.getItemId() == R.id.action_menu0){
                                                        if(type.equals("file") || type.equals("image")){
                                                            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                            builder.setTitle("다운로드").setMessage("다운로드 하시겠습니까?")
                                                                    .setPositiveButton("다운로드", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            URLDownloading(Uri.parse("http://43.201.55.113" + attachList.get(position).getName()), attachList.get(position).getName().substring(8));
                                                                        }
                                                                    })
                                                                    .setNegativeButton("취소", null)
                                                                    .show();
                                                        } else {
                                                            Toast.makeText(getApplicationContext(), "이미지와 파일만 다운로드할 수 있습니다.", Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else if (menuItem.getItemId() == R.id.action_menu1){
                                                        //댓글에 파일 첨부
                                                        long now = System.currentTimeMillis();
                                                        Date mDate = new Date(now);
                                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        String getTime = simpleDate.format(mDate);

                                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                                    if (success) {//저장에 성공한 경우
                                                                        new Thread(new Runnable() {
                                                                            public void run() {
                                                                                CardView.this.runOnUiThread(new Runnable() {
                                                                                    public void run() {
                                                                                    }
                                                                                });
                                                                            }
                                                                        }).start();
                                                                        finish();//인텐트 종료
                                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                                        Intent intent = getIntent(); //인텐트
                                                                        startActivity(intent); //액티비티 열기
                                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    } else {//저장에 실패한 경우
                                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };

                                                        //서버로 volley 를 이용해서 요청을 함
                                                        CardCommentRequest cardCommentRequest = new CardCommentRequest(pk, attachList.get(position).getName(), attachList.get(position).getType(), getTime, responseListener);
                                                        RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                                        queue.add(cardCommentRequest);

                                                    }else if (menuItem.getItemId() == R.id.action_menu2){
                                                        Intent i = new Intent(getApplicationContext(), AttachDeleteDialog.class);
                                                        i.putExtra("idx", attachList.get(position).getPk());
                                                        startActivity(i);
                                                    }

                                                    return false;
                                                }
                                            });
                                            popupMenu.show();
                                        }
                                    });
                                    if(type.equals("image")){
                                        name_tv.setText(attachList.get(position).getName().substring(8));
                                        String imageUrl = "http://43.201.55.113" + attachList.get(position).getName();
                                        Glide.with(CardView.this).load(imageUrl).into(iv);
                                        Uri uri = Uri.parse(imageUrl);
                                        date_tv.setText(attachList.get(position).getDate());

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                                                intent.putExtra("url", "http://43.201.55.113" + attachList.get(position).getName());
                                                startActivity(intent);
                                            }
                                        });
                                    } else if(type.equals("file")){
                                        name_tv.setText(attachList.get(position).getName().substring(8));
                                        String imageUrl = "http://43.201.55.113" + attachList.get(position).getName();
                                        iv.setVisibility(View.GONE);
                                        Uri uri = Uri.parse(imageUrl);
                                        date_tv.setText(attachList.get(position).getDate());

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), Mp4Activity.class);
                                                intent.putExtra("url", "http://43.201.55.113" + attachList.get(position).getName());
                                                startActivity(intent);
                                            }
                                        });
                                    } else if(type.equals("board")){
                                        name_tv.setText(attachList.get(position).getName());
                                        iv.setVisibility(View.GONE);
                                        date_tv.setText("보드");

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), Test.class);
                                                intent.putExtra("room_idx", attachList.get(position).getDate());
                                                intent.putExtra("room_title", attachList.get(position).getName());
                                                intent.putExtra("cover_show", "1");
                                                startActivity(intent);
                                            }
                                        });
                                    } else if(type.equals("card")){
                                        name_tv.setText(attachList.get(position).getName());
                                        iv.setVisibility(View.GONE);
                                        date_tv.setText("카드");

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), CardView.class);
                                                intent.putExtra("pk", attachList.get(position).getDate());
                                                startActivity(intent);
                                            }
                                        });
                                    } else if(type.equals("link")){
                                        iv.setVisibility(View.GONE);
                                        name_tv.setText(attachList.get(position).getName());

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                builder.setTitle("링크 연결").setMessage("링크에 연결 하시겠습니까?")
                                                        .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Uri uri = Uri.parse("http://"+attachList.get(position).getName());
                                                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                                                startActivity(it); // EditText에 적은 주소로 인터넷 연결
                                                            }
                                                        })
                                                        .setNegativeButton("취소", null)
                                                        .show();
                                            }
                                        });
                                    }

                                    else {
                                        Glide.with(getApplicationContext()).load(R.drawable.image_null).into(iv);
                                    }
                                }

                                @Override
                                public int getItemCount() {
                                    return attachList.size();
                                }
                            };
                            attach_rv.setAdapter(recyclerViewAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CardView.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CardView.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pk", getPk);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CardView.this);
        requestQueue.add(stringRequest);
    }

    private void loadComment(String getPk) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, COMMENT_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                String pk = item.getString("pk");
                                String bno = item.getString("bno");
                                String name = item.getString("name");
                                String type = item.getString("type");
                                String date = item.getString("date");

                                CardComment attachment = new CardComment();

                                attachment.setPk(pk);
                                attachment.setBno(bno);
                                attachment.setName(name);
                                attachment.setType(type);
                                attachment.setDate(date);

                                commentList.add(attachment);
                            }

                            comment_rv = findViewById(R.id.comment_rv);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CardView.this);
                            comment_rv.setLayoutManager(linearLayoutManager);

                            RecyclerView.Adapter<RecyclerView.ViewHolder> recyclerViewAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                @NonNull
                                @Override
                                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View itemView = LayoutInflater.from(CardView.this).inflate(R.layout.card_attach_item, parent, false);
                                    return new RecyclerView.ViewHolder(itemView) {
                                    };
                                }

                                @Override
                                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
                                    TextView name_tv = holder.itemView.findViewById(R.id.name);
                                    TextView date_tv = holder.itemView.findViewById(R.id.date);
                                    ImageView iv = holder.itemView.findViewById(R.id.image);
                                    ImageView menu = holder.itemView.findViewById(R.id.menu_btn);
                                    LinearLayout attach_item = holder.itemView.findViewById(R.id.attach_item);
                                    date_tv.setText(commentList.get(position).getDate());
                                    String type = commentList.get(position).getType();
                                    menu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {

                                            final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                                            popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                @Override
                                                public boolean onMenuItemClick(MenuItem menuItem) {
                                                    if (menuItem.getItemId() == R.id.action_menu1){
                                                        //댓글 수정

                                                    }else if (menuItem.getItemId() == R.id.action_menu2){
                                                        Intent i = new Intent(getApplicationContext(), CardCommentDeleteDialog.class);
                                                        i.putExtra("idx", commentList.get(position).getPk());
                                                        startActivity(i);
                                                    }

                                                    return false;
                                                }
                                            });
                                            popupMenu.show();
                                        }
                                    });
                                    if(type.equals("image")){
                                        name_tv.setText(commentList.get(position).getName().substring(8));
                                        String imageUrl = "http://43.201.55.113" + commentList.get(position).getName();
                                        Glide.with(CardView.this).load(imageUrl).into(iv);
                                        Uri uri = Uri.parse(imageUrl);

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                builder.setTitle("다운로드").setMessage("이미지를 다운로드 하시겠습니까?")
                                                        .setPositiveButton("다운로드", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                URLDownloading(uri, commentList.get(position).getName().substring(8));
                                                            }
                                                        })
                                                        .setNegativeButton("취소", null)
                                                        .show();
                                            }
                                        });

                                    } else if(type.equals("file")){
                                        name_tv.setText(commentList.get(position).getName().substring(8));
                                        String imageUrl = "http://43.201.55.113" + commentList.get(position).getName();
                                        iv.setVisibility(View.GONE);
                                        Uri uri = Uri.parse(imageUrl);

                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                builder.setTitle("다운로드").setMessage("파일을 다운로드 하시겠습니까?")
                                                        .setPositiveButton("다운로드", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                URLDownloading(uri, commentList.get(position).getName().substring(8));
                                                            }
                                                        })
                                                        .setNegativeButton("취소", null)
                                                        .show();
                                            }
                                        });
                                    } else if(type.equals("link")){
                                        iv.setVisibility(View.GONE);
                                        name_tv.setText(commentList.get(position).getName());
                                        attach_item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                builder.setTitle("링크 연결").setMessage("링크에 연결 하시겠습니까?")
                                                        .setPositiveButton("연결", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Uri uri = Uri.parse("http://"+commentList.get(position).getName());
                                                                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                                                startActivity(it); // EditText에 적은 주소로 인터넷 연결
                                                            }
                                                        })
                                                        .setNegativeButton("취소", null)
                                                        .show();
                                            }
                                        });
                                    } else if(type.equals("comment")){
                                        iv.setVisibility(View.GONE);
                                        name_tv.setText(commentList.get(position).getName());
                                    }
                                    else {
                                        Glide.with(getApplicationContext()).load(R.drawable.image_null).into(iv);
                                    }
                                }

                                @Override
                                public int getItemCount() {
                                    return commentList.size();
                                }
                            };
                            comment_rv.setAdapter(recyclerViewAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CardView.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CardView.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("pk", getPk);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CardView.this);
        requestQueue.add(stringRequest);
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

    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }

        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = getIntent();
        pk = intent.getStringExtra("pk");
        bno = intent.getStringExtra("bno");
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = simpleDate.format(mDate);

        Log.v("main onActivityResult", "onActivityResult 호출");
        if (resultCode == RESULT_OK) {

            Log.v("resultCode == RESULT_OK", "resultCode == RESULT_OK");
            if (requestCode == PICK_FOR_FILE) {

                Log.v("PICK_FOR_FILE", "requestCode == PICK_FOR_FILE");
                Uri fileUri = data.getData();
//                String path = getRealPathFromURI(fileUri);
                String name = getName(fileUri);

//                ImagePath = "/storage/emulated/0/Download/" + name;
                ImageName = name;

//                int SDK_INT = android.os.Build.VERSION.SDK_INT;
//                if (SDK_INT > 8) {
//                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                            .permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    //your codes here
//                    uploadFile(ImagePath);
//                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        CardView.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();//인텐트 종료
                                overridePendingTransition(0, 0);//인텐트 효과 없애기
                                Intent intent = getIntent(); //인텐트
                                startActivity(intent); //액티비티 열기
                                overridePendingTransition(0, 0);//인텐트 효과 없애기
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                CardAttachRequest cardAttachRequest = new CardAttachRequest(pk, "/Images/" + name, "file", getTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardView.this);
                queue.add(cardAttachRequest);
            }
            if (requestCode == PICK_FORM_ALBUM) {

                Log.v("PICK_FORM_ALBUM", "requestCode == PICK_FORM_ALBUM");
                Uri photoUri = data.getData();

                String path = getPath(photoUri);
                String name = getName(photoUri);

                ImagePath = path;
                ImageName = name;

                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here
                    uploadFile(ImagePath);
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        CardView.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                finish();//인텐트 종료
                                overridePendingTransition(0, 0);//인텐트 효과 없애기
                                Intent intent = getIntent(); //인텐트
                                startActivity(intent); //액티비티 열기
                                overridePendingTransition(0, 0);//인텐트 효과 없애기
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                CardAttachRequest cardAttachRequest = new CardAttachRequest(pk, "/Images/" + name, "image", getTime, responseListener);
                RequestQueue queue = Volley.newRequestQueue(CardView.this);
                queue.add(cardAttachRequest);

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

    private void loadList(String getBno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                Intent intent = getIntent();
                                pk = product.getString("pk".trim());
                                idx = product.getString("idx".trim());
                                list_idx = product.getString("list_idx".trim());
                                bno = product.getString("bno".trim());
                                title = product.getString("title".trim());
                                start_time = product.getString("start_time".trim());
                                end_time = product.getString("end_time".trim());
                                dateChecked = product.getString("date_checked".trim());
                                cover = product.getString("cover".trim());
                                String content = product.getString("content".trim());
                                String checklist = product.getString("checklist".trim());

                                cardCheckList = new ArrayList<CardCheckList>();
                                checkProgress = 0;

                                if(!checklist.equals("")){
                                    JSONArray jsonArray = new JSONArray(checklist);
                                    for(int i1 = 0; i1 < jsonArray.length(); i1++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i1);

                                        int idx = jsonObject.getInt("idx".trim());
                                        boolean is_check = jsonObject.getBoolean("is_check".trim());
                                        boolean is_edit = jsonObject.getBoolean("is_edit".trim());
                                        String get_content = jsonObject.getString("content".trim());

                                        CardCheckList card = new CardCheckList(idx, is_check, is_edit, get_content);
                                        cardCheckList.add(card);

                                        if(is_check){
                                            checkProgress = checkProgress + 1;
                                        } else{

                                        }
                                    }
                                } else {

                                }
                                back_btn = findViewById(R.id.back_btn);
                                menu_btn = findViewById(R.id.menu_btn);
                                cover_btn = findViewById(R.id.cover_btn);
                                cover_iv = findViewById(R.id.cover_image_view);
                                edit_button = findViewById(R.id.edit_button);
                                first_title = findViewById(R.id.title);
                                title_tv = findViewById(R.id.title_tv);
                                memo_tv = findViewById(R.id.memo_tv);
                                memo_et = findViewById(R.id.memo_et);
                                start_btn = findViewById(R.id.start_btn);
                                end_btn = findViewById(R.id.end_btn);
                                timeCheckBox = findViewById(R.id.date_checkbox);
                                end_btn_layout = findViewById(R.id.end_btn_layout);
                                label_btn = findViewById(R.id.label_btn);
                                label_rv = findViewById(R.id.label_rv);
                                attach_btn = findViewById(R.id.attach_btn);
                                attach_rv = findViewById(R.id.attach_rv);
                                comment_et = findViewById(R.id.comment_write);
                                comment_btn = findViewById(R.id.comment_write_btn);

                                add_list_layout = findViewById(R.id.list_edit_layout);
                                add_list_et = findViewById(R.id.add_plan_et);
                                add_list_btn = findViewById(R.id.add_plan_button);
                                add_list_rv = findViewById(R.id.add_list_rv);
                                progress_tv = findViewById(R.id.progress_tv);
                                progressBar = findViewById(R.id.progress_bar);

                                cardList = new ArrayList<Card>();

                                first_title.setText(title);
                                title_tv.setText(title);

                                if(cover.equals("")){
                                    cover_iv.setVisibility(View.GONE);
                                } if(cover.equals("1")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#7BC86C"));
                                } if(cover.equals("2")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#F5DD29"));
                                } if(cover.equals("3")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#FFAF3F"));
                                } if(cover.equals("4")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#EF7564"));
                                } if(cover.equals("5")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#CD8DE5"));
                                } if(cover.equals("6")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#5BA4CF"));
                                } if(cover.equals("7")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#29CCE5"));
                                } if(cover.equals("8")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#6DECA9"));
                                } if(cover.equals("9")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#FF8ED4"));
                                } if(cover.equals("10")){
                                    cover_iv.setVisibility(View.VISIBLE);
                                    cover_iv.setBackgroundColor(Color.parseColor("#172B4D"));
                                } if(cover.startsWith("/Images/")) {
                                    cover_iv.setBackgroundColor(Color.WHITE);
                                    cover_iv.setVisibility(View.VISIBLE);
                                    Glide.with(getApplicationContext()).load("http://43.201.55.113" + cover).into(cover_iv);
                                }

                                comment_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String et = comment_et.getText().toString();
                                        long now = System.currentTimeMillis();
                                        Date mDate = new Date(now);
                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String getTime = simpleDate.format(mDate);

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                    if (success) {//저장에 성공한 경우
                                                        new Thread(new Runnable() {
                                                            public void run() {
                                                                CardView.this.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                    }
                                                                });
                                                            }
                                                        }).start();
                                                        finish();//인텐트 종료
                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                        Intent intent = getIntent(); //인텐트
                                                        startActivity(intent); //액티비티 열기
                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                                                    } else {//저장에 실패한 경우
                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        CardCommentRequest cardCommentRequest = new CardCommentRequest(pk, et, "comment", getTime, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                        queue.add(cardCommentRequest);
                                    }
                                });

                                cover_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent1 = new Intent(getApplicationContext(), CoverActivity.class);
                                        intent1.putExtra("pk", pk);
                                        intent1.putExtra("cover", cover);
                                        startActivity(intent1);
                                    }
                                });

                                if(start_time.equals("null")){
                                    start_btn.setText("시작 시간");
                                }else {
                                    start_btn.setText(start_time + " 시작");
                                }
                                if(end_time.equals("null")){
                                    end_btn.setText("종료 시간");
                                } else{
                                    end_btn.setText(end_time + " 종료");
                                    timeCheckBox.setVisibility(View.VISIBLE);
                                }
                                start_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent1 = new Intent(getApplicationContext(), CalendarActivity.class);
                                        intent1.putExtra("pk", pk);
                                        intent1.putExtra("start_or_end", "start");
                                        intent1.putExtra("time", start_time);
                                        startActivity(intent1);
                                    }
                                });

                                end_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent1 = new Intent(getApplicationContext(), CalendarActivity.class);
                                        intent1.putExtra("pk", pk);
                                        intent1.putExtra("start_or_end", "end");
                                        intent1.putExtra("time", end_time);
                                        startActivity(intent1);
                                    }
                                });

                                label_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), LabelActivity.class);
                                        intent.putExtra("pk", pk);
                                        intent.putExtra("bno", bno);
                                        startActivity(intent);
                                    }
                                });

                                attach_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence info[] = new CharSequence[]{"보드 / 카드 첨부", "문서 스캔", "파일 선택", "사진 앨범 선택", "링크 첨부"};

                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CardView.this);
                                        builder.setTitle("첨부 파일 추가하기");
                                        builder.setItems(info, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        // 파일 선택
                                                        Intent intent_attach_bc = new Intent(getApplicationContext(), BoardCardList.class);
                                                        intent_attach_bc.putExtra("pk", pk);
                                                        startActivity(intent_attach_bc);
                                                        break;
                                                    case 1:
                                                        // 파일 선택
                                                        Intent intentScan = new Intent(getApplicationContext(), ScanActivity.class);
                                                        intentScan.putExtra("pk", pk);
                                                        startActivity(intentScan);
                                                        break;
                                                    case 2:
                                                        // 파일 선택
                                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                        intent.setType("*/*");
                                                        startActivityForResult(intent, PICK_FOR_FILE);
                                                        break;
                                                    case 3:
                                                        // 앨범에서 선택
                                                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                                                        intent1.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                                        intent1.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                        startActivityForResult(intent1, PICK_FORM_ALBUM);
                                                        break;
                                                    case 4:
                                                        // 링크 첨부
                                                        final EditText linkEdit = new EditText(CardView.this);

                                                        androidx.appcompat.app.AlertDialog.Builder clsBuilder = new androidx.appcompat.app.AlertDialog.Builder(CardView.this);
                                                        clsBuilder.setTitle("링크 첨부하기");
                                                        clsBuilder.setView( linkEdit );
                                                        clsBuilder.setPositiveButton("확인",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick( DialogInterface dialog, int which) {
                                                                        long now = System.currentTimeMillis();
                                                                        Date mDate = new Date(now);
                                                                        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                                        String getTime = simpleDate.format(mDate);

                                                                        String linkStr = linkEdit.getText().toString();
                                                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                try {
                                                                                    JSONObject jasonObject = new JSONObject(response);//php에 response
                                                                                    boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                                                    if (success) {//저장에 성공한 경우
                                                                                        new Thread(new Runnable() {
                                                                                            public void run() {
                                                                                                CardView.this.runOnUiThread(new Runnable() {
                                                                                                    public void run() {
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        }).start();
                                                                                        finish();//인텐트 종료
                                                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                                                        Intent intent = getIntent(); //인텐트
                                                                                        startActivity(intent); //액티비티 열기
                                                                                        overridePendingTransition(0, 0);//인텐트 효과 없애기
                                                                                    } else {//저장에 실패한 경우
                                                                                        Toast.makeText(getApplicationContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                                                        return;
                                                                                    }
                                                                                } catch (JSONException e) {
                                                                                    e.printStackTrace();
                                                                                }
                                                                            }
                                                                        };

                                                                        //서버로 volley 를 이용해서 요청을 함
                                                                        CardAttachRequest cardAttachRequest = new CardAttachRequest(pk, linkStr, "link", getTime, responseListener);
                                                                        RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                                                        queue.add(cardAttachRequest);
                                                                    }
                                                                });
                                                        clsBuilder.setNegativeButton("취소",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                    }
                                                                });
                                                        clsBuilder.show();
                                                        break;
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                });

                                if(dateChecked.equals("1")){
                                    timeCheckBox.setChecked(true);
                                }
                                if(dateChecked.equals("0")){
                                    timeCheckBox.setChecked(false);
                                }
                                else {

                                }

                                timeCheckBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(timeCheckBox.isChecked()){
                                            dateChecked = "1";
                                        } else{
                                            dateChecked = "0";
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
                                                        Toast.makeText(getApplicationContext(),"체크에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };

                                        //서버로 volley 를 이용해서 요청을 함
                                        CheckStatusRequest checkStatusRequest = new CheckStatusRequest(pk, dateChecked, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                        queue.add(checkStatusRequest);
                                    }
                                });

                                title_tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText txtEdit = new EditText(CardView.this);
                                        txtEdit.setText(title);

                                        androidx.appcompat.app.AlertDialog.Builder clsBuilder = new androidx.appcompat.app.AlertDialog.Builder(CardView.this);
                                        clsBuilder.setTitle("카드 제목 변경");
                                        clsBuilder.setView( txtEdit );
                                        clsBuilder.setPositiveButton("확인",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick( DialogInterface dialog, int which) {
                                                        title = txtEdit.getText().toString();
                                                        ((TextView) findViewById(R.id.title_tv)).setText(title);

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
                                                                    }
                                                                    else{//정보수정에 실패한 경우
                                                                        Toast.makeText(CardView.this,"제목 변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };

                                                        //서버로 volley 를 이용해서 요청을 함
                                                        CardTitleEditRequest listTitleEditRequest = new CardTitleEditRequest(pk, title, responseListener);
                                                        RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                                        queue.add(listTitleEditRequest);
                                                    }
                                                });
                                        clsBuilder.setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        clsBuilder.show();
                                    }
                                });
                                memo_tv.setText(content);

                                progress_tv.setText(String.valueOf(Math.round(checkProgress / (double) cardCheckList.size() * 100)) + "%");
                                progressBar.setProgress((int) (checkProgress / (double) cardCheckList.size() * 100));

                                add_list_rv.setLayoutManager(new LinearLayoutManager(CardView.this,RecyclerView.VERTICAL,false));

                                adapter = new CheckListAdapter(cardCheckList, getApplicationContext());
                                add_list_rv.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅

                                adapter.setOnItemClickListener(new CheckListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position, String isCheck) {
                                        if(isCheck.equals("true")) {
                                            checkProgress = checkProgress + 1;
                                        } if(isCheck.equals("false")) {
                                            checkProgress = checkProgress - 1;
                                        }

                                        progress_tv.setText((String.valueOf(Math.round(checkProgress / (double) cardCheckList.size() * 100))) + "%");
                                        progressBar.setProgress((int) (checkProgress / (double) cardCheckList.size() * 100));
                                    }
                                });

                                ItemTouchHelper.Callback callback =
                                        new CheckListItemMoveCallback((CheckListItemMoveCallback.ItemTouchHelperContract) adapter);
                                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                                touchHelper.attachToRecyclerView(add_list_rv);

                                adapter.notifyDataSetChanged();

                                back_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });

                                menu_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                                        getMenuInflater().inflate(R.menu.menu_card,popupMenu.getMenu());
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                if (menuItem.getItemId() == R.id.action_menu1) {
                                                    Intent i = new Intent(getApplicationContext(), CardPosEdit.class);
                                                    i.putExtra("pk", pk);
                                                    i.putExtra("idx", idx);
                                                    i.putExtra("list_idx", list_idx);
                                                    i.putExtra("bno", bno);
                                                    i.putExtra("list_title", list_title);
                                                    startActivity(i);
                                                } else if (menuItem.getItemId() == R.id.action_menu2){

                                                } else if (menuItem.getItemId() == R.id.action_menu3){
                                                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                    builder.setTitle("카드 보관").setMessage("해당 카드를 보관하시겠습니까?")
                                                            .setPositiveButton("보관", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            try {
                                                                                JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                                                boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                                                if (success) {//정보수정에 성공한 경우
                                                                                    Toast.makeText(getApplicationContext(), "카드가 보관되었습니다.", Toast.LENGTH_SHORT).show();
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
                                                                                    Toast.makeText(getApplicationContext(),"보관에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                                    return;
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    };

                                                                    //서버로 volley 를 이용해서 요청을 함
                                                                    ArchiveCardRequest cardDeleteRequest = new ArchiveCardRequest(pk, responseListener);
                                                                    RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                                                    queue.add(cardDeleteRequest);
                                                                }
                                                            })
                                                            .setNegativeButton("취소", null)
                                                            .show();
                                                }else if (menuItem.getItemId() == R.id.action_menu4){
                                                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(CardView.this);
                                                    builder.setTitle("삭제").setMessage("해당 카드를 삭제하시겠습니까?")
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
                                                                                    Toast.makeText(getApplicationContext(), "카드가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
                                                                    CardDeleteRequest cardDeleteRequest = new CardDeleteRequest(pk, responseListener);
                                                                    RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                                                    queue.add(cardDeleteRequest);
                                                                }
                                                            })
                                                            .setNegativeButton("취소", null)
                                                            .show();
                                                }

                                                return false;
                                            }
                                        });
                                        popupMenu.show();
                                    }
                                });

                                edit_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!edit_mode){ // 편집 버튼 클릭
                                            edit_mode = true;
                                            edit_button.setText("저장");
                                            memo_tv.setVisibility(View.GONE);
                                            memo_et.setVisibility(View.VISIBLE);
                                            memo_et.setText(content);
                                            start_btn.setVisibility(View.GONE);
                                            end_btn_layout.setVisibility(View.GONE);
                                            label_btn.setVisibility(View.GONE);
                                            label_rv.setVisibility(View.GONE);
                                            progress_tv.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            add_list_layout.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < cardCheckList.size(); i++){
                                                int card_idx = cardCheckList.get(i).getCard_idx();
                                                boolean isCheck = cardCheckList.get(i).getIsCheck();
                                                String content = cardCheckList.get(i).getContent();
                                                CardCheckList cardCheckList1 =
                                                        new CardCheckList(card_idx, isCheck, true, content);
                                                cardCheckList.set(i, cardCheckList1);
                                            }
                                            adapter.notifyDataSetChanged();

                                            add_list_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(add_list_et.getText().toString().equals("")){

                                                    } else {
                                                        v.getId();
                                                        CardCheckList cardCheckList1 =
                                                                new CardCheckList(cardCheckList.size() + 1, false, true, add_list_et.getText().toString());
                                                        cardCheckList.add(cardCheckList1);
                                                        adapter.notifyDataSetChanged();
                                                        add_list_et.setText("");
                                                        progress_tv.setText(String.valueOf(Math.round(checkProgress / (double) cardCheckList.size() * 100)) + "%");
                                                        progressBar.setProgress((int) (checkProgress / (double) cardCheckList.size() * 100));
                                                    }
                                                }
                                            });

                                        } else if(edit_mode){ // 저장 버튼 클릭
                                            try {
                                                //TODO 액티비티 화면 재갱신 시키는 코드
                                                Intent intent = getIntent();
                                                finish(); //현재 액티비티 종료 실시
                                                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                                                startActivity(intent); //현재 액티비티 재실행 실시
                                                overridePendingTransition(0, 0); //인텐트 애니메이션 없애기
                                            }
                                            catch (Exception e){
                                                e.printStackTrace();
                                            }
//                                            edit_mode = false;
//                                            edit_button.setText("편집");
//                                            memo_tv.setVisibility(View.VISIBLE);
//                                            memo_tv.setText(memo_et.getText().toString());
//                                            memo_et.setVisibility(View.GONE);
//                                            add_list_layout.setVisibility(View.GONE);
                                            for (int i = 0; i < cardCheckList.size(); i++){
                                                int card_idx = cardCheckList.get(i).getCard_idx();
                                                boolean isCheck = cardCheckList.get(i).getIsCheck();
                                                String content = cardCheckList.get(i).getContent();
                                                CardCheckList cardCheckList1 =
                                                        new CardCheckList(card_idx, isCheck, false, content);
                                                cardCheckList.set(i, cardCheckList1);
                                            }
                                            adapter.notifyDataSetChanged();

                                            String cardJsonArray = null;

                                            try {
                                                JSONArray jArray = new JSONArray();//배열이 필요할때
                                                for (int i = 0; i < cardCheckList.size(); i++) {
                                                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                                                    sObject.put("idx", cardCheckList.get(i).getCard_idx());
                                                    sObject.put("is_check", cardCheckList.get(i).getIsCheck());
                                                    sObject.put("is_edit", cardCheckList.get(i).getIsEdit());
                                                    sObject.put("content", cardCheckList.get(i).getContent());
                                                    jArray.put(sObject);
                                                }

                                                cardJsonArray = jArray.toString();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

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
                                                        }
                                                        else{//저장에 실패한 경우
                                                            Toast.makeText(getApplicationContext(),"저장에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            CardViewEditRequest cardViewEditRequest = new CardViewEditRequest(pk, memo_et.getText().toString(), cardJsonArray, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(CardView.this);
                                            queue.add(cardViewEditRequest);
                                        }
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CardView.this,"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CardView.this,"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bno", getBno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(CardView.this);
        requestQueue.add(stringRequest);
    }
}
