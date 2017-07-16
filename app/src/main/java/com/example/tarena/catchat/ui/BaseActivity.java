package com.example.tarena.catchat.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.constant.Constant;

import butterknife.ButterKnife;

/**
 * Created by tarena on 2017/7/5.
 */

public abstract class BaseActivity extends AppCompatActivity {
    TextView titleTV;
    public abstract int getLayoutID();
    Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //控制做页面跳转的动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));

        setContentView(getLayoutID());
        ButterKnife.bind(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);


        ActionBar ab = getSupportActionBar();
        titleTV = new TextView(this);
        titleTV.setText("Title");
        titleTV.setTextColor(Color.WHITE);
        titleTV.setTextSize(20);
        titleTV.setGravity(Gravity.CENTER);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(300, 150, Gravity.CENTER);
        ab.setCustomView(titleTV,lp);
        ab.setDisplayHomeAsUpEnabled(false);
    }
    protected void showBackBtn(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    //关于toast和log
    public void toast(String text) {
        if (!TextUtils.isEmpty(text)) {
            toast.setText(text);
            toast.show();
        }
    }

    public void log(String log) {
        if (Constant.DEBUG) {
            Log.d("TAG", "从" + this.getClass().getSimpleName() + "打印的日志：" + log);
        }
    }

    public void log(int code, String message) {
        String log = "错误信息：" + code + ":" + message;
        log(log);
    }

    public void toastAndLog(String text, String log) {
        toast(text);
        log(log);
    }

    public void toastAndLog(String text, int code, String message) {
        toast(text);
        log(code, message);
    }
    //关于界面跳转
    public void jumpTo(Class<?> clazz,boolean isFinish,boolean isAnimation){
        Intent intent = new Intent(this, clazz);
        if (isAnimation){
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        }else {
            startActivity(intent);
        }
        if (isFinish){
            finish();
        }
    }
    public void jumpTo(Intent intent, boolean isFinish, boolean isAnimation) {

        if (isAnimation) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        }else {
            startActivity(intent);
        }

        if (isFinish) {
            finish();
        }
    }

    /**
     * 是用来判定EditText是否都输入了内容
     *
     * @return true 意味着EditText...有未输入内容
     * false 意味着所有的EditText都输入了内容
     */
    public boolean isEmpty(EditText... ets) {

        for (EditText et : ets) {
            String text = et.getText().toString();
            if (TextUtils.isEmpty(text)) {
                //et.setError("请输入完整！");

                SpannableString ss = new SpannableString("请输入完整！");
                //你给ss使用哪一种span效果
                ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 3, SpannableString
                        .SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new BackgroundColorSpan(Color.BLACK), 0, 6, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ImageSpan(this, R.drawable.boy), 5, 6, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

                et.setError(ss);

                return true;
            }
        }

        return false;
    }
    //点击空白区域收起软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null){
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                    0);
        }

        return super.onTouchEvent(event);
    }
}
