package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EditLabelRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/EditLabel.php";
    private Map<String, String> parameters;


    public EditLabelRequest(String pk, String idx, String title, String color_str_kor, String color_str, String color_int, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("pk", pk);
        parameters.put("idx", idx);
        parameters.put("title", title);
        parameters.put("color_str_kor", color_str_kor);
        parameters.put("color_str", color_str);
        parameters.put("color_int", color_int);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
