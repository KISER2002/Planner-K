package com.example.project2.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.project2.Adapter.StarBoardAdapter;
import com.example.project2.Adapter.WorkSpaceAdapter;
import com.example.project2.OnListItemSelectedInterface;
import com.example.project2.R;
import com.example.project2.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class fragHome extends Fragment implements OnListItemSelectedInterface {
    private View view;

    private static final String PRODUCT_URL = "http://43.201.55.113/WorkSpaceList.php";

    private ImageView writeBtn;

    private ArrayList<WorkSpace> workspaceList;
    private ArrayList<WorkSpace> starBoardList;
    private WorkSpaceAdapter adapter;
    private StarBoardAdapter adapter1;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    public static Context context;

    private String myId;

    SessionManager sessionManager;

    private String TAG = "프래그먼트";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, container, false);

        writeBtn = view.findViewById(R.id.add_workspace_btn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MakeWorkspaceDialog.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        context = getContext();

        recyclerView = view.findViewById(R.id.workspace_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        workspaceList = new ArrayList<>();

        adapter = new WorkSpaceAdapter(getActivity(), workspaceList, this::OnItemSelected);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        recyclerView1 = view.findViewById(R.id.star_workspace_rv);
        recyclerView1.setLayoutManager(new LinearLayoutManager(context));

        starBoardList = new ArrayList<>();

        adapter1 = new StarBoardAdapter(getActivity(), starBoardList, this::OnItemSelected);
        recyclerView1.setAdapter(adapter1);

        adapter1.notifyDataSetChanged();

        sessionManager = new SessionManager(getActivity());

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mId = user.get(sessionManager.ID);
        String mName = user.get(sessionManager.NAME);

        myId = mId;

        loadProducts(mId);

    }

    @Override
    public void OnItemSelected(View v, int position) {

    }

    private void loadProducts(String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                String getIdx = product.getString("idx");
                                String getTitle = product.getString("title");
                                String getRoomImg = product.getString("image");
                                String getStar = product.getString("star");
                                String getCoverShow = product.getString("cover_show");

                                WorkSpace workSpace = new WorkSpace();

                                workSpace.setIdx(getIdx);
                                workSpace.setTitle(getTitle);
                                workSpace.setRoomImg(getRoomImg);
                                workSpace.setStar(getStar);
                                workSpace.setCover_show(getCoverShow);

                                workspaceList.add(workSpace);
                                adapter.notifyDataSetChanged();

                                if (getStar.equals("1")) {

                                    WorkSpace workSpace1 = new WorkSpace();
                                    workSpace1.setIdx(getIdx);
                                    workSpace1.setTitle(getTitle);
                                    workSpace1.setRoomImg(getRoomImg);
                                    workSpace1.setStar(getStar);
                                    workSpace1.setCover_show(getCoverShow);

                                    starBoardList.add(workSpace1);
                                    adapter1.notifyDataSetChanged();
                                }

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
                params.put("mID", userId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }
}