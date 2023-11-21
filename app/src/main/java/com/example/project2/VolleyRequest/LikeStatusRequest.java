package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LikeStatusRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/LikeStatus.php";
    private Map<String, String> parameters;


    public LikeStatusRequest(String bno, String readUser, String is_like, String date, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("bno", bno);
        parameters.put("readUser", readUser);
        parameters.put("is_like", is_like);
        parameters.put("date", date);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
