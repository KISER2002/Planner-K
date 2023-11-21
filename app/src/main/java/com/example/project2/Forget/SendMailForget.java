package com.example.project2.Forget;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.GMailSender;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendMailForget extends AppCompatActivity {
    String user = "mailsendproject2@gmail.com"; // 보내는 계정의 id
    String password = "qnnxjhlkrmaqbwjv"; // 보내는 계정의 pw

    GMailSender gMailSender = new GMailSender(user, password);
    String emailCode = gMailSender.getEmailCode();
    public void sendSecurityCode(Context context, String sendTo) {
        try {
            gMailSender.sendMail("이메일 인증번호 입니다.", "안녕하세요.\n 인증번호는 [" + emailCode + "] 입니다.", sendTo);
            Toast.makeText(context, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e) {
            Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            Toast.makeText(context, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}