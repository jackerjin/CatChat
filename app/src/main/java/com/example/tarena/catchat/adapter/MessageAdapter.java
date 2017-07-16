package com.example.tarena.catchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.util.TimeUtil;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by tarena on 2017/7/13.
 */

public class MessageAdapter extends MyBaseAdapter {
    ArrayList<EMMessage> messages;
    MyUser currentUser;
    MyUser toUser;

    public MessageAdapter(Context context, List datasource, MyUser user) {
        super(context, datasource);
        messages = (ArrayList<EMMessage>) datasource;
        toUser = user;
        currentUser = BmobUser.getCurrentUser(MyApp.CONTEXT, MyUser.class);


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_message, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
//把所有消息体全部隐藏
        vh.contentImageView.setVisibility(View.GONE);
        vh.contentTextView.setVisibility(View.GONE);

        EMMessage m = messages.get(i);
        if (i>0) {
            EMMessage preM = messages.get(i - 1);
            if (m.getMsgTime()-preM.getMsgTime()<60*1000) {
                vh.timeTV.setVisibility(View.GONE);
            }else vh.timeTV.setVisibility(View.VISIBLE);
        }
        //显示时间
        vh.timeTV.setText(TimeUtil.getTime(m.getMsgTime()));

        if (m.getType()== EMMessage.Type.TXT){
            vh.contentTextView.setVisibility(View.VISIBLE);
            //得到消息体
            EMTextMessageBody body = (EMTextMessageBody) m.getBody();
            vh.contentTextView.setText(body.getMessage());

        }else if (m.getType()== EMMessage.Type.IMAGE){
            vh.contentImageView.setVisibility(View.VISIBLE);
            //得到消息体
            EMImageMessageBody body = (EMImageMessageBody) m.getBody();


            if (m.getFrom().equals(BmobUser.getCurrentUser(MyApp.CONTEXT).getUsername())){

                ImageLoader.getInstance().displayImage(body.getRemoteUrl(),vh.contentImageView);
            }else {
                Bitmap b = BitmapFactory.decodeFile(body.getLocalUrl());
                vh.contentImageView.setImageBitmap(b);

            }
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vh.messagelayout.getLayoutParams();

        if (m.getFrom().equals(BmobUser.getCurrentUser(MyApp.CONTEXT).getUsername())){
            vh.headIV.setX(MyApp.KScreenW-vh.headIV.getLayoutParams().width);
            vh.contentTextView.setBackgroundResource(R.drawable.chatto_bg_normal);
            vh.contentImageView.setBackgroundResource(R.drawable.chatto_bg_normal);
            lp.leftMargin = dip2px(100);
            lp.rightMargin = dip2px(50);
            vh.messagelayout.setGravity(Gravity.RIGHT);
            ImageLoader.getInstance().displayImage(currentUser.getAvatar(), vh.headIV);


        }else  {
            ImageLoader.getInstance().displayImage(toUser.getAvatar(), vh.headIV);
            vh.headIV.setX(0);
            vh.contentTextView.setBackgroundResource(R.drawable.chatfrom_bg_normal);
            vh.contentImageView.setBackgroundResource(R.drawable.chatfrom_bg_normal);
            lp.leftMargin = dip2px(50);
            lp.rightMargin = dip2px(100);
            vh.messagelayout.setGravity(Gravity.LEFT);


        }



        return view;
    }

    public class ViewHolder {
        @BindView(R.id.textView2)
        TextView timeTV;
        @BindView(R.id.headIV)
        ImageView headIV;
        @BindView(R.id.textview)
        TextView contentTextView;
        @BindView(R.id.contentIV)
        ImageView contentImageView;
        @BindView(R.id.messagelayout)
        RelativeLayout messagelayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this,view);

        }
    }
    public  int dip2px(float dpValue) {
        final float scale = MyApp.CONTEXT.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
