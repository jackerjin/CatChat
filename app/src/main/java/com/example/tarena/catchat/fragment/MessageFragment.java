package com.example.tarena.catchat.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.adapter.MailAdapter;
import com.example.tarena.catchat.ui.ChatActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends BaseFragment {
    @BindView(R.id.listview_homefragment)
    ListView listview_mail;
    List<String> list=new ArrayList<>();
    ArrayList<EMConversation> conversations=new ArrayList<>();

    public MessageFragment() {
        // Required empty public constructor
    }



    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void init() {
        this.conversations.clear();
        Map<String, EMConversation> conversations =  EMClient.getInstance().chatManager().getAllConversations();
        for (String key : conversations.keySet()) {
            Log.i("TAG","key----"+key);
            this.conversations.add(conversations.get(key));


        }
        final MailAdapter adapter=new MailAdapter(getActivity(),this.conversations);
        listview_mail.setBackgroundColor(Color.BLUE);
        listview_mail.setAdapter(adapter);
        listview_mail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                EMConversation c =  MessageFragment.this.conversations.get(i);
                MailAdapter.ViewHolder vh = (MailAdapter.ViewHolder) view.getTag();
                //指定会话消息未读数清零)
                intent.putExtra("username",c.conversationId());
                intent.putExtra("toUser", vh.user);
                c.markAllMessagesAsRead();
                startActivity(intent);
            }
        });


        //添加监听

        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(final List<EMMessage> messages) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //收到消息
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
    }
}
