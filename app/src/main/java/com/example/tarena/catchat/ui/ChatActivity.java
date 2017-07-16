package com.example.tarena.catchat.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.adapter.MessageAdapter;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.MyUser;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.tarena.catchat.R.id.listview;

public class ChatActivity extends BaseActivity {
    @BindView(R.id.sendInfoET)
    EditText sendInfoET;
    @BindView(listview)
    ListView listView;
    private MyUser toUser;
    ArrayList<EMMessage> messages = new ArrayList<>();
    MessageAdapter adapter;
    @Override
    public int getLayoutID() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toUser = (MyUser) getIntent().getExtras().get("toUser");
        titleTV.setText(toUser.getUsername());
        showBackBtn();
        //得到和对方的会话对象
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation
                (toUser.getUsername());
        //获取会话所有的消息
        if (conversation!=null) {
            messages.addAll(conversation.getAllMessages());
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                EMMessage m = messages.get(i);
                if (m.getType()== EMMessage.Type.IMAGE) {
                    EMImageMessageBody body = (EMImageMessageBody) m.getBody();
                    if (m.getFrom().equals(toUser.getUsername())) {
                        AlertDialog.Builder ab=new AlertDialog.Builder(ChatActivity.this);
                        ImageView iv = new ImageView(ChatActivity.this);
                        ImageLoader.getInstance().displayImage(body.getRemoteUrl(), iv);
                        ab.setView(iv);
                        ab.create().show();
                    }else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(ChatActivity.this);
                        ImageView iv = new ImageView(ChatActivity.this);
                        Bitmap b = BitmapFactory.decodeFile(body.getLocalUrl());
                        iv.setImageBitmap(b);
                        ad.setView(iv);
                        ad.create().show();

                    }
                }

            }
        });
        //添加监听

        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(final List<EMMessage> messages) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //收到消息
                        ChatActivity.this.messages.addAll(messages);
                        adapter.notifyDataSetChanged();

                    }
                });

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

        adapter = new MessageAdapter(MyApp.CONTEXT, messages,toUser);
     listView.setAdapter(adapter);
        listView.setDividerHeight(0);
//        listview.setDividerHeight(0);
    }





    @OnClick({R.id.sendBtn, R.id.imageBtn, R.id.voiceBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:

                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                EMMessage message = EMMessage.createTxtSendMessage(sendInfoET.getText().toString(),
                        toUser.getUsername());

                //发送消息
                EMClient.getInstance().chatManager().sendMessage(message);
                messages.add(message);

                adapter.notifyDataSetChanged();

                break;
            case R.id.imageBtn:

                //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接操作
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission
                        .READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //注意第二个参数没有双引号
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest
                            .permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, 101);
                }

                break;
            case R.id.voiceBtn:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, 101);
        } else {
            // Permission Denied
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == RESULT_OK) {
            if (arg0 == 101) {
                Uri uri = arg2.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images
                        .Media.DATA}, null, null, null);
                cursor.moveToNext();
                String filePath = cursor.getString(0);


                EMMessage message = EMMessage.createImageSendMessage(filePath, false, toUser.getUsername());

                EMClient.getInstance().chatManager().sendMessage(message);

                messages.add(message);
                adapter.notifyDataSetChanged();


            }
            //TODO 拍照返回或地图截图返回，只要得到图片的本地路径
            //通过调用showBlogImage就可以将图片放到blogImgs中显示
        }
    }
}
