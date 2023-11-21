package com.example.project2.Home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.project2.VolleyRequest.ListCopyRequest;
import com.example.project2.VolleyRequest.ListSortRequest;
import com.example.project2.databinding.ActivityBoardMenuBinding;
import com.example.project2.databinding.ActivitySortListBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ListSort extends AppCompatActivity {
    private static final String LIST_URL = "http://43.201.55.113/LoadBoard.php";

    ActivitySortListBinding binding;

    String idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySortListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        idx = intent.getStringExtra("idx");

        binding.nameAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "name_asc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });

        binding.nameDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "name_desc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });

        binding.dateAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "date_asc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });

        binding.dateDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "date_desc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });

        binding.dueAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "due_asc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });
        binding.dueDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//php에 response
                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                            if (success) {//저장에 성공한 경우
                                new Thread(new Runnable() {
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            public void run() {
                                            }
                                        });
                                    }
                                }).start();
                                Toast.makeText(getApplicationContext(),"리스트가 정렬되었습니다.",Toast.LENGTH_SHORT).show();
                                finish();
                            } else {//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(), "복사에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                ListSortRequest listSortRequest = new ListSortRequest(idx, "due_desc", responseListener);
                RequestQueue queue = Volley.newRequestQueue(ListSort.this);
                queue.add(listSortRequest);
            }
        });


    }
}
