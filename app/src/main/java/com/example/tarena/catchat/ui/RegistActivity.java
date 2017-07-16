package com.example.tarena.catchat.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.bean.MyUser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

public class RegistActivity extends BaseActivity {

    @BindView(R.id.et_regist_username)
    EditText etRegistUsername;
    @BindView(R.id.et_regist_password)
    EditText etRegistPassword;
    @BindView(R.id.et_regist_okPassword)
    EditText etRegistOkPassword;
    @BindView(R.id.radioGroup)
    RadioGroup rgGender;

    @Override
    public int getLayoutID() {
        return R.layout.activity_regist;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        showBackBtn();
    }

    @OnClick(R.id.btn_regist)
    public void onViewClicked() {

        //3)构建实体类(MyUser)对象
        final MyUser user = new MyUser();

        user.setUsername(etRegistUsername.getText().toString());
        //是否MD5加密？取决于同学自己的设计
        user.setPassword(etRegistPassword.getText().toString());

        boolean gender = true;
        if (rgGender.getCheckedRadioButtonId() == R.id.rb_girl) {
            gender = false;
        }

        user.setGender(gender);
        //进行注册
        user.signUp(this, new SaveListener() {

            @Override
            public void onSuccess() {
                //进行登录
                user.login(RegistActivity.this, new SaveListener() {

                    @Override
                    public void onSuccess() {
                        //登录成功

                        //跳转界面，跳转到MainActivity
                        jumpTo(MainActivity.class, true,true);
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {

                        toastAndLog("登录失败", arg0, arg1);
                    }
                });
            }

            @Override
            public void onFailure(int arg0, String arg1) {

                switch (arg0) {
                    case 202:
                        toast("用户名重复");
                        break;

                    default:
                        toastAndLog("注册用户失败稍后重试", arg0, arg1);
                        break;
                }



            }
        });

    }

}
