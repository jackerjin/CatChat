package com.example.tarena.catchat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.util.TimeUtil;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by tarena on 2017/7/13.
 */

public class MailAdapter extends MyBaseAdapter<EMConversation> {
    ArrayList<EMConversation> conversations;
    public MailAdapter(Context context, ArrayList<EMConversation> datasource) {
        super(context, datasource);
        conversations = datasource;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
    ViewHolder viewHolder=null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_mail_list, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) view.getTag();

        }
        EMConversation conversation = conversations.get(position);
        BmobQuery<MyUser> query = new BmobQuery<>();
        query.addWhereEqualTo("username", conversation.conversationId());
      final   ViewHolder finalViewHolder=viewHolder;
      final   ViewHolder finalViewHolder1=viewHolder;
        query.findObjects(context, new FindListener<MyUser>() {
            @Override
            public void onSuccess(List<MyUser> list) {
                MyUser myUser = list.get(0);
                finalViewHolder.user=myUser;
                String avatar = myUser.getAvatar();
                Picasso.with(MyApp.CONTEXT).load(avatar).into(finalViewHolder1.imageview_mial_photo);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        viewHolder.textview_name.setText(conversation.conversationId());
        int unreadMsgCount = conversation.getUnreadMsgCount();
        Log.i("TAG", "getView: " + unreadMsgCount);
        if (unreadMsgCount == 0) {
            viewHolder.textview_Unread.setVisibility(View.GONE);
        }else {
            viewHolder.textview_Unread.setVisibility(View.VISIBLE);
            viewHolder.textview_Unread.setText(String.valueOf(unreadMsgCount));
        }

        //获取此会话的所有消息
        EMMessage message = conversation.getLastMessage();

        if (message.getType()==EMMessage.Type.TXT){
            EMTextMessageBody body= (EMTextMessageBody) message.getBody();
            viewHolder.textview_content.setText(body.getMessage());

        }else if (message.getType()==EMMessage.Type.IMAGE){
            viewHolder.textview_content.setText("[图片]");
        }
        String time = TimeUtil.getTime(message.getMsgTime());
        viewHolder.textview_time.setText(time);
        return view;
    }
    public class ViewHolder{
        public MyUser user;
        @BindView(R.id.imageview_mial_photo)
        ImageView imageview_mial_photo;
        @BindView(R.id.textview_Unread)
        TextView textview_Unread;
        @BindView(R.id.textview_name)
        TextView textview_name;
        @BindView(R.id.textview_content)
        TextView textview_content;;
        @BindView(R.id.textview_time)
        TextView textview_time;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
