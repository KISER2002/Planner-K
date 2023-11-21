package com.example.project2.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.BoardAdapter;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragBoard extends Fragment implements OnListItemSelectedInterface {
    private View view;

    private static final String PRODUCT_URL = "http://43.201.55.113/BoardList.php";

    private ImageButton writeBtn;

    private ArrayList<Board> boardList;
    private BoardAdapter boardAdapter;
    private RecyclerView recyclerView;
    public static Context context;

    private int newBno = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_board, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView = view.findViewById(R.id.board_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        boardList = new ArrayList<>();

        boardAdapter = new BoardAdapter(getActivity(), boardList, this::OnItemSelected);
        recyclerView.setAdapter(boardAdapter);

        boardAdapter.notifyDataSetChanged();

        loadProducts();

    }

    @Override
    public void OnItemSelected(View v, int position) {

    }

    private void loadProducts() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            if(array.length() > 0) {
                                JSONObject newItem = array.getJSONObject(0);
                                newBno = newItem.getInt("idx");
                            }

                            writeBtn = view.findViewById(R.id.writing);
                            writeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(), BoardWrite.class);
                                    i.putExtra("newBno", newBno + 1);
                                    startActivity(i);
                                }
                            });

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                String idx = item.getString("idx");
                                String title = item.getString("title");
                                String content = item.getString("content");
                                String image = item.getString("image");
                                String date = item.getString("date");
                                Integer hit = item.getInt("hit");
                                String writer = item.getString("writer");
                                String writerName = item.getString("userName");
                                String profileImg = item.getString("profileImg");
                                String comment = item.getString("comment");

                                Board board = new Board();

                                board.setIdx(idx);
                                board.setTitle(title);
                                board.setContent(content);
                                board.setPhoto(image);
                                board.setDate(date);
                                board.setHit(hit);
                                board.setWriter(writer);
                                board.setWriterName(writerName);
                                board.setProfileImg(profileImg);
                                board.setComment(comment);

                                boardList.add(board);
                                boardAdapter.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}