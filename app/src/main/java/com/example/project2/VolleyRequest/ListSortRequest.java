package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ListSortRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/ListSort.php";
    private Map<String, String> parameters;


    public ListSortRequest(String idx, String sort, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("idx", idx);
        parameters.put("sort", sort);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
