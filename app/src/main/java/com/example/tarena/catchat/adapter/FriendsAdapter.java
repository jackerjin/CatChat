package com.example.tarena.catchat.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.bean.MyUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by tarena on 2017/7/12.
 */

public class FriendsAdapter extends MyBaseAdapter {
ArrayList<String> friends;
    public FriendsAdapter(Context context, List datasource) {
        super(context, datasource);
        friends = (ArrayList<String>) datasource;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_friends, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            String username = friends.get(position);

            //通过用户名查询用户详情
            BmobQuery<MyUser> query = new BmobQuery<MyUser>();
            query.addWhereEqualTo("username", username);

            query.findObjects(MyApp.CONTEXT, new FindListener<MyUser>() {
                @Override
                public void onSuccess(List<MyUser> list) {
                    if (list.size() > 0) {
                        final MyUser user = list.get(0);
                        vh.user=user;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoader.getInstance().displayImage(user.getAvatar(), vh.headIV);
                                vh.nickTV.setText(user.getNick());
                                vh.usernameTV.setText(user.getUsername());
                            }
                        });

                    }


                }

                @Override
                public void onError(int i, String s) {

                }
            });


            return convertView;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("日期格式不正确");
        }
    }

    public class ViewHolder {
        public MyUser user;
        @BindView(R.id.headIV)
        ImageView headIV;
        @BindView(R.id.nickTV)
        TextView nickTV;
        @BindView(R.id.usernameTV)
        TextView usernameTV;

        public ViewHolder(View convertView) {


            ButterKnife.bind(this, convertView);
        }


    }
}
