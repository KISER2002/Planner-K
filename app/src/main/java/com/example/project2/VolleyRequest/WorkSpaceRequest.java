package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class WorkSpaceRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/WorkSpace.php";
    private Map<String, String> parameters;


    public WorkSpaceRequest(String title, String writer, String roomImg, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("title", title);
        parameters.put("writer", writer);
        parameters.put("roomImg", roomImg);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
