package com.example.tarena.catchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.bean.MyUser;

import cn.bmob.v3.listener.SaveListener;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载图片
        setContentView(R.layout.activity_launch);
        autoLogin();
    }

    private void autoLogin() {
        SharedPreferences sp=getSharedPreferences("test", Activity.MODE_PRIVATE);
        String username=sp.getString("username","");
        String password = sp.getString("password", "");
        if (username!=null&&password!=null){
            MyUser user= new MyUser();
            user.setUsername(username);
            user.setPassword(password);

            user.login(this, new SaveListener() {

                @Override
                public void onSuccess() {

                    //跳转至 MainActivity
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                    //结束当前的 Activity
                    LaunchActivity.this.finish();


                }

                @Override
                public void onFailure(int arg0, String arg1) {
                    //登录失败

                    //跳转至 LoginActivity
                    Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                    startActivity(intent);
                    //结束当前的 Activity
                    LaunchActivity.this.finish();



                }
            });
        }else{//从来没有登录过

            //跳转至 LoginActivity
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
            //结束当前的 Activity
            LaunchActivity.this.finish();



        }
    }
}
