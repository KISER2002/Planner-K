package com.example.project2.VolleyRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserEditRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://43.201.55.113/UserEdit.php";
    private Map<String, String> parameters;


    public UserEditRequest(String id, String profileImg,String userName, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("userID", id);
        parameters.put("profileImg", profileImg);
        parameters.put("userName", userName);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
