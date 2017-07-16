package com.example.tarena.catchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.bean.MyUser;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.lg_tv_username)
    EditText etUsername;
    @BindView(R.id.lg_tv_password)
    EditText etPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

  private void login(String username,String password){
      MyUser user = new MyUser();
      user.setUsername(username);
      user.setPassword(password);
      user.login(this, new SaveListener() {
          @Override
          public void onSuccess() {
              SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
              //实例化SharedPreferences.Editor对象
              SharedPreferences.Editor editor = sharedPreferences.edit();
              //用putString的方法保存数据
              editor.putString("username", etUsername.getText().toString());
              editor.putString("password", etPassword.getText().toString());
              //提交数据
              editor.commit();
              jumpTo(MainActivity.class,false,true);
              //登录环信
              loginEaseMob();
          }

          @Override
          public void onFailure(int i, String s) {
//登录失败


              //根据不同的arg0,尽量给出详细的提示
              switch (i) {
                  case 101:
                      toast("用户名或密码错误");
                      break;

                  default:
                      toastAndLog("登录失败", i, s);
                      break;
              }

          }
      });
  }

    private void loginEaseMob() {
        EMClient.getInstance().login(etUsername.getText().toString(), "admin", new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.i("TAG+main", "登录聊天服务器成功");
            }

            @Override
            public void onError(int code, String error) {
                Log.i("TAG", "登录聊天服务器失败"+code+error);
                //登录失败则因为未注册
                try {
                    // 调用sdk注册方法
                    EMClient.getInstance().createAccount(etUsername.getText().toString(),
                            "admin");//同步方法
                } catch (final Exception e) {
                    //注册失败
                    Log.i("ivan", "注册失败: " + e);
                    return;
                }
                //注册成功！
                Log.i("ivan", "注册成功！");

                loginEaseMob();
            }



            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }
    @OnClick(R.id.btn_login)
    public void Onlogin(){
        if (isEmpty(etUsername,etPassword)){
            return;
        }
        login(etUsername.getText().toString(),etPassword.getText().toString());
    }

    @OnClick(R.id.tv_login_register)
    public void onRegister(){
        jumpTo(RegistActivity.class,false,true);
    }
}
