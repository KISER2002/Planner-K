package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommentBoardReadRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/CommentBoardRead.php";
    private Map<String,String> map;

    public CommentBoardReadRequest(String idx, Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("idx",idx);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
