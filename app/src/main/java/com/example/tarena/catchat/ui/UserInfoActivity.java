package com.example.tarena.catchat.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a.a.V;
import com.example.tarena.catchat.R;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.thirdparty.CircleImageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.volley.toolbox.ImageLoader;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.iv_userinfo_avatar)
    CircleImageView ivUserinfoAvatar;
    @BindView(R.id.iv_userinfo_avatareditor)
    ImageView ivUserinfoAvatareditor;
    @BindView(R.id.tv_userinfo_nickname)
    TextView tvUserinfoNickname;
    @BindView(R.id.et_userinfo_nickname)
    EditText etUserinfoNickname;
    @BindView(R.id.ib_userinfo_confirm)
    ImageButton ibUserinfoConfirm;
    @BindView(R.id.ib_userinfo_cancel)
    ImageButton ibUserinfoCancel;
    @BindView(R.id.ll_userinfo_editnicknamecontainer)
    LinearLayout llUserinfoEditnicknamecontainer;
    @BindView(R.id.iv_userinfo_nicknameeditor)
    ImageView ivUserinfoNicknameeditor;
    @BindView(R.id.tv_userinfo_username)
    TextView tvUserinfoUsername;
    @BindView(R.id.iv_userinfo_gender)
    ImageView ivUserinfoGender;
    //如果要修改性别，请参考修改昵称的做法
    @BindView(R.id.btn_userinfo_update)
    Button btnUserinfoUpdate;
    @BindView(R.id.btn_userinfo_chat)
    Button btnUserinfoChat;
    @BindView(R.id.btn_userinfo_black)
    Button btnUserinfoBlack;
    @BindView(R.id.btn_userinfo_addFriend)
    Button btnAddFriend;
    //me：从SettingFragment跳转过来
    //friend: 从FriendFragmetn跳转过来
    //stranger: 从AddFriendActivity或者NewFriendActivity跳转过来
    String from;
    String username;
    MyUser user;//根据username属性获得相对应的用户
    String cameraPath;//拍摄头像照片时SD卡的路径
    String avatarUrl;//上传头像照片完毕后，头像照片在服务器的网站
    private Uri imageUri;
    @OnClick(R.id.btn_userinfo_addFriend)
    public void addFriendAction(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(UserInfoActivity.this
                                    .username
                            , "请求添加好友");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserInfoActivity.this, "请求发送完成！", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        }).start();
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            username = getIntent().getStringExtra("username");
        initView();
        initActionBar();

    }

    private void initActionBar() {
        titleTV.setText("个人资料");
        showBackBtn();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 101) {
            String filePath = "";
            if (data != null) {
                //图库选图
                //对于部分手机来说，在安卓原生的拍照程序基础上做了修改
                //导致拍照的照片会随着arg2返回到这里
                Uri uri = data.getData();
                if (uri != null) {
                    if (!uri.getPath().equals(imageUri.getPath())) {
                        //图库
                        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                        cursor.moveToNext();
                        filePath = cursor.getString(0);
                        cursor.close();
                    }else {
                        //拍照
                        //拍照的路径依然是cameraPath
                        filePath = cameraPath;
                    }
                } else{
                    Bundle bundle = data.getExtras();
                    //bitmap是拍照回来的照片
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    //TODO 将bitmap存储到SD卡
                }
            }else{
                //相机拍照
                filePath = cameraPath;
            }
            final BmobFile bf = new BmobFile(new File(filePath));
            bf.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    avatarUrl = bf.getFileUrl(UserInfoActivity.this);
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                            .displayImage(avatarUrl,ivUserinfoAvatar);
                }

                @Override
                public void onFailure(int i, String s) {
                toastAndLog("上传头像失败",i,s);
                }
            });
        }
    }
    @OnClick(R.id.iv_userinfo_nicknameeditor)
    public void setNickname(View v){
        String nickname = tvUserinfoNickname.getText().toString();
        tvUserinfoNickname.setVisibility(View.INVISIBLE);
        llUserinfoEditnicknamecontainer.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(nickname)){
            etUserinfoNickname.setHint("请输入昵称...");
        }else{
            etUserinfoNickname.setText(nickname);
        }
        ivUserinfoNicknameeditor.setVisibility(View.GONE);
    }
    @OnClick(R.id.ib_userinfo_confirm)
    public void saveNickname(View v){
        String nickname = etUserinfoNickname.getText().toString();
        etUserinfoNickname.setText("");
        tvUserinfoNickname.setVisibility(View.VISIBLE);
        tvUserinfoNickname.setText(nickname);
        llUserinfoEditnicknamecontainer.setVisibility(View.INVISIBLE);
        ivUserinfoNicknameeditor.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.ib_userinfo_cancel)
    public void cancelNickname(View v){
        etUserinfoNickname.setText("");
        tvUserinfoNickname.setVisibility(View.VISIBLE);
        llUserinfoEditnicknamecontainer.setVisibility(View.INVISIBLE);
        ivUserinfoNicknameeditor.setVisibility(View.VISIBLE);
    }
    @OnClick(R.id.btn_userinfo_update)
    public void update(View v){
        if(avatarUrl!=null){
            user.setAvatar(avatarUrl);
        }

        user.setNick(tvUserinfoNickname.getText().toString());


        user.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                toast("资料更新完成");

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                log(arg0,arg1);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                    }
                }, 1500);

            }
        });
    }

    @OnClick(R.id.btn_userinfo_chat)
    public void chat(View v){

    }
    @OnClick(R.id.iv_userinfo_avatar)
    public void setAvatar(View v) {
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".jpg");
        cameraPath = file.getAbsolutePath();
        imageUri = Uri.fromFile(file);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent chooser = Intent.createChooser(intent1, "选择头像...");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
        startActivityForResult(chooser, 101);
    }

    private void initView() {
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.findObjects(this, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                user = list.get(0);
                //根据user中的内容设定界面
                //设定用户头像
                String avatar = user.getAvatar();
                if (TextUtils.isEmpty(avatar)) {
                    ivUserinfoAvatar.setImageResource(R.drawable.ic_launcher);
                } else {
                    com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                            .displayImage(avatar, ivUserinfoAvatar);
                }

                //user的昵称
                String nickname = user.getNick();
                tvUserinfoNickname.setText(nickname);
                llUserinfoEditnicknamecontainer.setVisibility(View.INVISIBLE);

                //user的用户名
                String username = user.getUsername();
                tvUserinfoUsername.setText(username);
                //user的性别
                Boolean gender = user.getGender();
                if (gender) {
                    ivUserinfoGender.setImageResource(R.drawable.boy);
                } else {
                    ivUserinfoGender.setImageResource(R.drawable.girl);
                }
                //昵称的铅笔
                if (username.equals(BmobUser.getCurrentUser(MyApp.CONTEXT).getUsername())) {
                    ivUserinfoNicknameeditor.setVisibility(View.VISIBLE);
                    ivUserinfoAvatareditor.setVisibility(View.VISIBLE);
                    btnUserinfoUpdate.setVisibility(View.VISIBLE);
                }else {
                    ivUserinfoAvatareditor.setVisibility(View.INVISIBLE);
                    btnUserinfoUpdate.setVisibility(View.GONE);
                    ivUserinfoNicknameeditor.setVisibility(View.INVISIBLE);
                    //判断是否为好友
                    loadFriends();
                }

            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("查询用户信息失败", i, s);
            }
        });
    }

    private void loadFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<String> usernames = (ArrayList<String>) EMClient.getInstance().contactManager()
                            .getAllContactsFromServer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String username : usernames) {
                            if (username.equals(UserInfoActivity.this.username)) {
                                //是好友
                                btnUserinfoBlack.setVisibility(View.VISIBLE);
                                btnUserinfoChat.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        btnAddFriend.setVisibility(View.VISIBLE);

                    }
                });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
