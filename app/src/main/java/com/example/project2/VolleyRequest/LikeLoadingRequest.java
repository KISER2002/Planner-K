package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LikeLoadingRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/LikeRead.php";
    private Map<String,String> map;

    public LikeLoadingRequest(String bno, String readUser, Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("bno",bno);
        map.put("readUser", readUser);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
