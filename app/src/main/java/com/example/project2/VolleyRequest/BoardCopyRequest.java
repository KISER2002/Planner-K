package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardCopyRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/BoardCopy.php";
    private Map<String, String> parameters;


    public BoardCopyRequest(String idx, String title, String writer, String image, String keep_card, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("idx", idx);
        parameters.put("title", title);
        parameters.put("writer", writer);
        parameters.put("image", image);
        parameters.put("keep_card", keep_card);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}