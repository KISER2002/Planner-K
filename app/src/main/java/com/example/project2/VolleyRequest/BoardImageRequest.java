package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardImageRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/BoardImage.php";
    private Map<String, String> parameters;


    public BoardImageRequest(String idx, String bno, String image, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("idx", idx);
        parameters.put("bno", bno);
        parameters.put("image", image);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
