package com.example.tarena.catchat.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.fragment.FindFragment;
import com.example.tarena.catchat.fragment.FriendFragment;
import com.example.tarena.catchat.fragment.MessageFragment;
import com.example.tarena.catchat.fragment.SettingFragment;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.viewPagerId)
    ViewPager MainViewPager;
    ArrayList<Fragment> fragments = new ArrayList<>();
    @BindView(R.id.rg_main_footer)
    RadioGroup MainRG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleTV.setText("首页");
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        fragments.add(new MessageFragment());
        fragments.add(new FriendFragment());
        fragments.add(new FindFragment());
        fragments.add(new SettingFragment());
        MainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RadioButton rb = (RadioButton) MainRG.getChildAt(position);
                titleTV.setText(rb.getText());
                switch (position) {
                    case 0:
                        MainRG.check(R.id.radio0);
                        break;
                    case 1:
                        MainRG.check(R.id.radio1);
                        break;
                    case 2:
                        MainRG.check(R.id.radio2);
                        break;
                    case 3:
                        MainRG.check(R.id.radio3);
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        MainViewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.radio0, R.id.radio1, R.id.radio2, R.id.radio3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.radio0:
                MainViewPager.setCurrentItem(0, true);
                break;
            case R.id.radio1:
                MainViewPager.setCurrentItem(1, true);
                break;
            case R.id.radio2:
                MainViewPager.setCurrentItem(2, true);
                break;
            case R.id.radio3:
                MainViewPager.setCurrentItem(3, true);
                break;
        }
        RadioButton rb = (RadioButton) MainRG.getChildAt(MainViewPager.getCurrentItem());
        titleTV.setText(rb.getText());

    }
    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener{
        @Override
        public void onConnected() {
            Log.i("main", "onConnected: 链接成功！！！");
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                        } else {
                            //当前 不可用，请检查网络设置
                        }
                    }
                }
            });
        }
    }
}
