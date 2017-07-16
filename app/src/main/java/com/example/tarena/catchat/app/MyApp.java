package com.example.tarena.catchat.app;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.android.volley.toolbox.ImageLoader;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by tarena on 2017/7/5.
 */

public class MyApp extends Application {
    public static MyApp CONTEXT;
    public static int KScreenW;
    public static int KScreenH;
//    public static MediaPlayer player;
    public static RadioGroup mainRG;
    public static ArrayList<String> requests = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
        WindowManager wm = (WindowManager)
                getSystemService(Context.WINDOW_SERVICE);

        KScreenW = wm.getDefaultDisplay().getWidth();
        KScreenH = wm.getDefaultDisplay().getHeight();
        //个人账户：303fa54fc82a1ba90bf104de07594b15
        //老师账户：148b0e8f4e67c576b1c47debe0922312
        Bmob.initialize(this, "148b0e8f4e67c576b1c47debe0922312");
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动接受服务器推送服务
        BmobPush.startWork(this);
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(configuration);
        //
        EMOptions options = new EMOptions();
// 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

//初始化
        EMClient.getInstance().init(this, options);
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
}
