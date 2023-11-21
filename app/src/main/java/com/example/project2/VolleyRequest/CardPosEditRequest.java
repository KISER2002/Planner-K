package com.example.project2.VolleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CardPosEditRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/CardPosUpdate.php";
    private Map<String, String> parameters;


    public CardPosEditRequest(String old_idx, String old_list_idx, String new_idx, String new_list_idx, String pk, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("old_idx", old_idx);
        parameters.put("old_list_idx", old_list_idx);
        parameters.put("new_idx", new_idx);
        parameters.put("new_list_idx", new_list_idx);
        parameters.put("pk", pk);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
