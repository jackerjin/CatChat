package com.example.tarena.catchat.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.ui.LoginActivity;
import com.example.tarena.catchat.ui.UserInfoActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.tv_setting_name)
    TextView tvSettingUsername;
    @BindView(R.id.iv_setting_nameEdit)
    ImageView ivSettingEditorusername;
    @BindView(R.id.tv_setting_notice)
    TextView tvSettingNotification;
    @BindView(R.id.iv_setting_swithnotice)
    ImageView ivSettingEditornotification;
    @BindView(R.id.tv_setting_voice)
    TextView tvSettingSound;
    @BindView(R.id.iv_setting_swithvoice)
    ImageView ivSettingEditorsound;
    @BindView(R.id.tv_setting_vibrate)
    TextView tvSettingVibrate;
    @BindView(R.id.iv_setting_swithvibrate)
    ImageView ivSettingEditorvibrate;
    @BindView(R.id.btn_setting_logout)
    Button btnSettingLogout;
    Unbinder unbinder;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    @OnClick(R.id.btn_setting_logout)
    public void onViewClicked(View view){
        SharedPreferences sharedPreferences = baseActivity.getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");

        //提交当前数据
        editor.apply();
        //退出环信
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("main", "onSuccess: 退出登录成功！");
            }

            @Override
            public void onError(int code, String error) {
                Log.i("main", "onSuccess: 退出失败");
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });

        //跳转至 LoginActivity
        Intent intent = new Intent(baseActivity, LoginActivity.class);
        startActivity(intent);
        //结束当前的 Activity
        baseActivity.finish();

    }
    @OnClick(R.id.iv_setting_nameEdit)
    public void onEditUsernameClicked() {

        Intent intent = new Intent(getActivity(),UserInfoActivity.class);
        intent.putExtra("from", "me");
        baseActivity.jumpTo(intent,false,true);


    }
}
