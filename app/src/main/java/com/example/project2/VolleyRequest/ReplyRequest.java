package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReplyRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/Reply.php";
    private Map<String, String> parameters;


    public ReplyRequest(String bno, String parent, String writer, String comment, String date, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("bno", bno);
        parameters.put("parent", parent);
        parameters.put("writer", writer);
        parameters.put("comment", comment);
        parameters.put("date", date);

    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
