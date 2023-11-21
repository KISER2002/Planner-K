package com.example.project2.Account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.project2.A.Test;
import com.example.project2.MyPage.UserEdit;
import com.example.project2.R;
import com.example.project2.SessionManager;

import java.util.HashMap;

public class fragAccount extends Fragment {
    private View view;
    Context context;

    private String TAG = "프래그먼트";

    private TextView name, email;
    private Button userEditBtn, changePwBtn, settingBtn, logoutBtn;
    private ImageView profileImg;
    private String imageUrl;
    SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_account, container, false);

        context = container.getContext();

        sessionManager = new SessionManager(context);

        profileImg = view.findViewById(R.id.profile_img);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        userEditBtn = view.findViewById(R.id.user_edit_btn);
        changePwBtn = view.findViewById(R.id.change_pw_btn);
        settingBtn = view.findViewById(R.id.user_setting_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);

        userEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserEdit
                        .class);
                startActivity(intent);
            }
        });

        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ConfirmPw.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
            }
        });

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();

        context = getContext();

        sessionManager = new SessionManager(context);

        profileImg = view.findViewById(R.id.profile_img);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String mName = user.get(sessionManager.NAME);
        String mEmail = user.get(sessionManager.ID);
        String mProfile = user.get(sessionManager.PROFILE);

        name.setText(mName);
        email.setText(mEmail);
        if (mProfile.equals("basic_image")) {
            Glide.with(context).load(R.drawable.profile_img).override(130, 130).into(profileImg);
        } else {
            imageUrl = "http://43.201.55.113" + mProfile;
            Glide.with(context).load(imageUrl).override(130, 130).into(profileImg);
        }
    }

}
