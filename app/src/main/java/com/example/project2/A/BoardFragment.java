package com.example.project2.A;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project2.Adapter.ListAdapter;
import com.example.project2.Board.BoardImage;
import com.example.project2.Home.ArchiveAllCardDialog;
import com.example.project2.Home.ArchiveListDialog;
import com.example.project2.Home.AttachDeleteDialog;
import com.example.project2.Home.BoardMenu;
import com.example.project2.Home.Card;
import com.example.project2.Home.CardPosEdit;
import com.example.project2.Home.EditListActivity;
import com.example.project2.Home.ListCopy;
import com.example.project2.Home.ListItem;
import com.example.project2.Home.ListMoveActivity;
import com.example.project2.Home.ListSort;
import com.example.project2.Home.MakeCardDialog;
import com.example.project2.Home.MakeListDialog;
import com.example.project2.Home.MoveAllCard;
import com.example.project2.Home.WorkSpaceActivity;
import com.example.project2.R;
import com.example.project2.VolleyRequest.BoardDeleteRequest;
import com.example.project2.VolleyRequest.CardDeleteRequest;
import com.example.project2.VolleyRequest.CardPosEditRequest;
import com.example.project2.VolleyRequest.CardPosEditRequest2;
import com.example.project2.VolleyRequest.CardRequest;
import com.example.project2.VolleyRequest.ListPosEditRequest;
import com.example.project2.VolleyRequest.ListTitleEditRequest;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;
import com.woxthebox.draglistview.DragItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardFragment extends Fragment {
    private static final String LIST_URL = "http://43.201.55.113/LoadingList.php";
    private static final String LOADING_COVER_SHOW = "http://43.201.55.113/LoadingCoverShow.php";
    private final String TAG = this.getClass().getSimpleName(); // BoardFragment
    private static int sCreatedItems = 0;
    private BoardView mBoardView;
    private TextView boardTitle;
    private ImageButton addListBtn, menuBtn;
    private int mColumns;
    private int currentColumn = 1;
    private int startColumn = 1;
    private boolean mGridLayout;

    private String getIntentIdx, getIntentTitle, cover_show;
    private int lastListIdx;

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setHasOptionsMenu(true);
    }

    private void loadCoverShow(String getBno) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOADING_COVER_SHOW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject item = array.getJSONObject(i);

                                String idx = item.getString("idx");
                                String title = item.getString("title");
                                String cover_show = item.getString("cover_show");

                                loadList(idx, cover_show);
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
                params.put("bno", getBno);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void loadList(String getBno, String getCover_show) {
//        Log.d(TAG, "loadList: 시작");
        Intent intent = getActivity().getIntent();
        getIntentIdx = intent.getStringExtra("room_idx");
        getIntentTitle = intent.getStringExtra("room_title");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //카드 리스트 고유(pk)키값을 저장하는 해쉬
                            HashMap<Integer, Integer> hashMapColumnPk = new HashMap<Integer, Integer>();
                            //카드 리스트 인덱스 값을 저장하는 해쉬
                            HashMap<Integer, Integer> hashMapColumnIdx = new HashMap<Integer, Integer>();
                            //카드리스트 크기를 저장하는 해쉬
                            HashMap<Integer, Integer> hashMapCardListSize = new HashMap<Integer, Integer>();
                            //카드의 인덱스 값을 저장하는 해쉬
                            HashMap<String, String> hashMapCardIdx = new HashMap<String, String>();
                            //카드의 고유(pk)키값을 저장하는 해쉬
                            HashMap<String, String> hashMapCardPk = new HashMap<String, String>();

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject product = array.getJSONObject(i);

                                int getPk = product.getInt("pk");
                                int getIdx = product.getInt("idx");
                                String getPkStr = product.getString("pk");
                                String getIdxStr = product.getString("idx");
                                String getBno = product.getString("bno");
                                String getTitle = product.getString("title");
                                String getList = product.getString("list");
                                String getArchive = product.getString("archive");

                                long id = sCreatedItems;
                                final ArrayList<Pair<Long, String>> mItemArray = new ArrayList<>();
                                final ArrayList<Card> cardList = new ArrayList<>();

                                hashMapColumnPk.put(i, getPk);
                                hashMapColumnIdx.put(i, getIdx);

                                lastListIdx = getIdx;

                                if(!getList.equals("null")){
                                    JSONArray jsonArray = new JSONArray(getList);
                                    for(int i1 = 0; i1 < jsonArray.length(); i1++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i1);

                                        int pk = jsonObject.getInt("pk".trim());
                                        int idx = jsonObject.getInt("idx".trim());
                                        int list_idx = jsonObject.getInt("list_idx".trim());
                                        String bno = jsonObject.getString("bno".trim());
                                        String title = jsonObject.getString("title".trim());

                                        String stringValue =
                                                "{\"pk\":\"" + pk + "\",\"idx\":\"" + idx + "\",\"list_idx\":\"" + list_idx +
                                                        "\",\"bno\":\"" + bno + "\",\"title\":\"" + title + "\",\"list_title\":\"" + getTitle +
                                                        "\"}\n";

                                        id = sCreatedItems++;
                                        mItemArray.add(new Pair<>(id, String.valueOf(jsonObject)));

                                        Card card = new Card(idx, list_idx, bno, title);
                                        cardList.add(card);
                                        hashMapCardIdx.put(i + "/" + i1, String.valueOf(idx));
                                        hashMapCardPk.put(i + "/" + i1, String.valueOf(pk));
                                    }
                                } else {

                                }

                                hashMapCardListSize.put(i, cardList.size());

                                final ItemAdapter listAdapter = new ItemAdapter(getActivity(), mItemArray, mGridLayout ? R.layout.a_grid_item : R.layout.a_column_item, R.id.item_layout, true);
                                final View header = View.inflate(getActivity(), R.layout.a_column_header, null);
                                ((TextView) header.findViewById(R.id.text)).setText(getTitle);
                                ((TextView) header.findViewById(R.id.text)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final EditText txtEdit = new EditText(getActivity());
                                        txtEdit.setText(getTitle);

                                        AlertDialog.Builder clsBuilder = new AlertDialog.Builder(getActivity());
                                        clsBuilder.setTitle("리스트명 변경");
                                        clsBuilder.setView( txtEdit );
                                        clsBuilder.setPositiveButton("확인",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick( DialogInterface dialog, int which) {
                                                        String strText = txtEdit.getText().toString();
                                                        ((TextView) header.findViewById(R.id.text)).setText(strText);

                                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    JSONObject jasonObject = new JSONObject(response);//Register php에 response
                                                                    boolean success = jasonObject.getBoolean("success");//Register php에 sucess
                                                                    if (success) {//정보수정에 성공한 경우
                                                                        new Thread(new Runnable() {
                                                                            public void run() {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    public void run() {
                                                                                    }
                                                                                });
                                                                            }
                                                                        }).start();
                                                                    }
                                                                    else{//정보수정에 실패한 경우
                                                                        Toast.makeText(getActivity(),"제목 변경에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        };

                                                        //서버로 volley 를 이용해서 요청을 함
                                                        ListTitleEditRequest listTitleEditRequest = new ListTitleEditRequest(String.valueOf(getPk), strText, responseListener);
                                                        RequestQueue queue = Volley.newRequestQueue(getActivity());
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
                                long finalId = id;
                                ((ImageButton) header.findViewById(R.id.add_card_btn)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), MakeCardDialog.class);
                                        intent.putExtra("idx", getPk);
                                        intent.putExtra("bno", getBno);
                                        if(cardList.size()==0){
                                            intent.putExtra("pos", 0);
                                        } else {
                                            intent.putExtra("pos", cardList.get(cardList.size() - 1).getIdx());
                                        }
                                        startActivity(intent);
                                    }
                                });

                                ((ImageButton) header.findViewById(R.id.list_menu_btn)).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final PopupMenu popupMenu = new PopupMenu(getContext(), v);
                                        getActivity().getMenuInflater().inflate(R.menu.list_menu,popupMenu.getMenu());
                                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                if (menuItem.getItemId() == R.id.action_menu1){ // 리스트 복사
                                                    Intent intent = new Intent(getActivity(), ListCopy.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                } else if (menuItem.getItemId() == R.id.action_menu2){ // 리스트 이동
                                                    Intent intent = new Intent(getActivity(), ListMoveActivity.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                } else if (menuItem.getItemId() == R.id.action_menu3){ // 리스트 내 카드 순서 정렬
                                                    Intent intent = new Intent(getActivity(), ListSort.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                } else if (menuItem.getItemId() == R.id.action_menu4){ // 리스트 내 모든 카드 이동
                                                    Intent intent = new Intent(getActivity(), MoveAllCard.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                } else if (menuItem.getItemId() == R.id.action_menu5){ // 리스트 내 모든 카드 보관
                                                    Intent intent = new Intent(getActivity(), ArchiveAllCardDialog.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                } else if (menuItem.getItemId() == R.id.action_menu6){ // 리스트 보관
                                                    Intent intent = new Intent(getActivity(), ArchiveListDialog.class);
                                                    intent.putExtra("pk", getPkStr);
                                                    intent.putExtra("idx", getIdxStr);
                                                    intent.putExtra("bno", getBno);
                                                    intent.putExtra("title", getTitle);
                                                    startActivity(intent);
                                                }

                                                return false;
                                            }
                                        });
                                        popupMenu.show();
                                    }
                                });

                                LinearLayoutManager layoutManager = mGridLayout ? new GridLayoutManager(getContext(), 4) : new LinearLayoutManager(getContext());
                                ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                                        .setLayoutManager(layoutManager)
                                        .setHasFixedItemSize(false)
                                        .setColumnBackgroundColor(Color.TRANSPARENT)
                                        .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                                        .setHeader(header)
                                        .setColumnDragView(header)
                                        .build();

                                mBoardView.addColumn(columnProperties);
                                mColumns++;

                                mBoardView.setBoardListener(new BoardView.BoardListener() {
                                    @Override
                                    public void onItemDragStarted(int column, int row) {
                                        //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                                        if (fromColumn != toColumn || fromRow != toRow) {

                                            String oldIdx = hashMapCardIdx.get(fromColumn + "/" + fromRow);
                                            String oldListIdx = String.valueOf(hashMapColumnPk.get(fromColumn));
                                            String newIdx = "null";
                                            if(hashMapCardIdx.containsKey(toColumn + "/" + toRow) == false){
                                                hashMapCardIdx.put(toColumn + "/" + toRow, String.valueOf(hashMapCardListSize.get(toColumn) + 1));
                                            } else {
                                                hashMapCardIdx.get(toColumn + "/" + toRow);
                                            }
                                            newIdx = hashMapCardIdx.get(toColumn + "/" + toRow);

                                            String newListIdx = String.valueOf(hashMapColumnPk.get(toColumn));
                                            String cardPk = hashMapCardPk.get(fromColumn + "/" + fromRow);

                                            if(oldListIdx.equals(newListIdx)){ //TODO 같은 리스트 내에서의 포지션 변경일 경우
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jasonObject = new JSONObject(response);//php에 response
                                                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                            if (success) {//저장에 성공한 경우
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                            }
                                                                        });
                                                                    }
                                                                }).start();
//                                                                resetBoard();
//                                                                Intent intent = new Intent(getActivity(), Test.class);
//                                                                intent.putExtra("room_idx", getIntentIdx);
//                                                                intent.putExtra("room_title", getIntentTitle);
//                                                                startActivity(intent); // start same activity
//                                                                getActivity().finish(); // destroy older activity
//                                                                getActivity().overridePendingTransition(0, 0);
                                                            } else {//저장에 실패한 경우
                                                                Toast.makeText(getContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                //서버로 volley 를 이용해서 요청을 함
                                                CardPosEditRequest2 cardPosEditRequest = new CardPosEditRequest2(oldIdx, newIdx, newListIdx, cardPk, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(getActivity());
                                                queue.add(cardPosEditRequest);

                                            } else { //TODO 서로 다른 리스트 간의 카드 변경일 경우
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jasonObject = new JSONObject(response);//php에 response
                                                            boolean success = jasonObject.getBoolean("success");//php에 sucess
                                                            if (success) {//저장에 성공한 경우
                                                                new Thread(new Runnable() {
                                                                    public void run() {
                                                                        getActivity().runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                            }
                                                                        });
                                                                    }
                                                                }).start();
//                                                            hashMapColumnIdx.put(toColumn , hashMapColumnIdx.get(fromColumn));
//                                                            hashMapCardIdx.put(toRow , hashMapCardIdx.get(fromRow));
//                                                            resetBoard();
                                                            } else {//저장에 실패한 경우
                                                                Toast.makeText(getContext().getApplicationContext(), "생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                //서버로 volley 를 이용해서 요청을 함
                                                CardPosEditRequest cardPosEditRequest = new CardPosEditRequest(oldIdx, oldListIdx, newIdx, newListIdx, cardPk, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(getActivity());
                                                queue.add(cardPosEditRequest);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {

                                    }

                                    @Override
                                    public void onItemChangedColumn(int oldColumn, int newColumn) {

                                    }

                                    @Override
                                    public void onFocusedColumnChanged(int oldColumn, int newColumn) {

                                    }

                                    @Override
                                    public void onColumnDragStarted(int position) {
                                        startColumn = position;
                                    }

                                    @Override
                                    public void onColumnDragChangedPosition(int oldPosition, int newPosition) {

                                    }

                                    @Override
                                    public void onColumnDragEnded(int position) {
                                        currentColumn = position;
                                        if(!(startColumn == position)){
                                            String oldListIdx = String.valueOf(hashMapColumnIdx.get(startColumn));

                                            String newListIdx = String.valueOf(hashMapColumnIdx.get(currentColumn));

                                            String cardPk = String.valueOf(hashMapColumnPk.get(startColumn));

                                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);//SeatEdit php에 response
                                                        boolean success = jasonObject.getBoolean("success");//SeatEdit php에 sucess
                                                        if (success) {//저장에 성공한 경우
                                                            new Thread(new Runnable() {
                                                                public void run() {
                                                                    getActivity().runOnUiThread(new Runnable() {
                                                                        public void run() {
                                                                        }
                                                                    });
                                                                }
                                                            }).start();
//                                                            hashMapColumnIdx.put(toColumn , hashMapColumnIdx.get(fromColumn));
//                                                            hashMapCardIdx.put(toRow , hashMapCardIdx.get(fromRow));
//                                                            resetBoard();
                                                        }
                                                        else{//저장에 실패한 경우
                                                            Toast.makeText(getContext().getApplicationContext(),"생성에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };

                                            //서버로 volley 를 이용해서 요청을 함
                                            ListPosEditRequest listPosEditRequest = new ListPosEditRequest(oldListIdx,newListIdx, cardPk, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                                            queue.add(listPosEditRequest);
                                        }
                                    }
                                });
                                mBoardView.setBoardCallback(new BoardView.BoardCallback() {
                                    @Override
                                    public boolean canDragItemAtPosition(int column, int dragPosition) {
                                        // Add logic here to prevent an item to be dragged
                                        return true;
                                    }

                                    @Override
                                    public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                                        // Add logic here to prevent an item to be dropped
                                        return true;
                                    }
                                });
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
                params.put("bno", getBno);
                params.put("cover_show", getCover_show);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a_board_layout, container, false);

        mBoardView = view.findViewById(R.id.board_view);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        resetBoard();
    }

    private void resetBoard() {
        mBoardView.clearBoard();
        mBoardView.setCustomDragItem(mGridLayout ? null : new MyDragItem(getActivity(), R.layout.a_column_item));
        mBoardView.setCustomColumnDragItem(mGridLayout ? null : new MyColumnDragItem(getActivity(), R.layout.a_column_drag_layout));
        mBoardView.setVerticalScrollbarPosition(2);

        boardTitle = getActivity().findViewById(R.id.title);
        addListBtn = getActivity().findViewById(R.id.add_list_btn);
        menuBtn = getActivity().findViewById(R.id.setting_btn);

        Intent intent = getActivity().getIntent();
        getIntentIdx = intent.getStringExtra("room_idx");
        getIntentTitle = intent.getStringExtra("room_title");
        cover_show = intent.getStringExtra("cover_show");

//        boardTitle.setText(getIntentTitle);
        if(getIntentTitle.equals("")){

        } else {
            loadCoverShow(getIntentIdx);
        }

        addListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakeListDialog.class);
                intent.putExtra("idx", lastListIdx + 1);
                intent.putExtra("room_idx", getIntentIdx);
                startActivity(intent);
            }
        });

//        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), BoardMenu.class);
//                intent.putExtra("room_idx", getIntentIdx);
//                resultLauncher.launch(intent);
//            }
//        });

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BoardMenu.class);
                intent.putExtra("room_idx", getIntentIdx);
                startActivity(intent);
            }
        });

    }

//    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result){
//                    if(result.getResultCode() == Activity.RESULT_OK){
//
//                        Intent intent = result.getData();
//                        String getRoom_idx = intent.getStringExtra("room_idx");
//                        String getCover_show = intent.getStringExtra("cover_show");
//
//                        loadList(getRoom_idx, getCover_show);
//
//                    }
//                }
//            });

    private static class MyColumnDragItem extends DragItem {

        MyColumnDragItem(Context context, int layoutId) {
            super(context, layoutId);
            setSnapToTouch(false);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            LinearLayout clickedLayout = (LinearLayout) clickedView;
            View clickedHeader = clickedLayout.getChildAt(0);
            RecyclerView clickedRecyclerView = (RecyclerView) clickedLayout.getChildAt(1);

            View dragHeader = dragView.findViewById(R.id.drag_header);
            ScrollView dragScrollView = dragView.findViewById(R.id.drag_scroll_view);
            LinearLayout dragLayout = dragView.findViewById(R.id.drag_list);

            Drawable clickedColumnBackground = clickedLayout.getBackground();
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground);
            }

            Drawable clickedRecyclerBackground = clickedRecyclerView.getBackground();
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground);
            }

            dragLayout.removeAllViews();

            ((TextView) dragHeader.findViewById(R.id.text)).setText(((TextView) clickedHeader.findViewById(R.id.text)).getText());
//            ((TextView) dragHeader.findViewById(R.id.item_count)).setText(((TextView) clickedHeader.findViewById(R.id.item_count)).getText());

            for (int i = 0; i < clickedRecyclerView.getChildCount(); i++) {
                View view = View.inflate(dragView.getContext(), R.layout.a_column_item, null);
                ((TextView) view.findViewById(R.id.text)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.text)).getText());
                dragLayout.addView(view);

                if (i == 0) {
                    dragScrollView.setScrollY(-clickedRecyclerView.getChildAt(i).getTop());
                }
            }

            dragView.setPivotY(0);
            dragView.setPivotX(clickedView.getMeasuredWidth() / 2);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            super.onStartDragAnimation(dragView);
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            super.onEndDragAnimation(dragView);
            dragView.animate().scaleX(1).scaleY(1).start();
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);

            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }
    }
}
