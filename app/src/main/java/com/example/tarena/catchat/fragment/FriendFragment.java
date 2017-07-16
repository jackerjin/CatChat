package com.example.tarena.catchat.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.adapter.FriendsAdapter;
import com.example.tarena.catchat.app.MyApp;
import com.example.tarena.catchat.ui.ChatActivity;
import com.example.tarena.catchat.ui.RegistActivity;
import com.example.tarena.catchat.ui.RequestActivity;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends BaseFragment {

    @BindView(R.id.listview1)
    ListView listview;

    private List<String> usernames;
    private TextView requestCountTV;
    private FriendsAdapter adapter;
    public FriendFragment() {

        // Required empty public constructor
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_friend, container, false);
        return view;
    }




    @Override
    public void init() {
        super.init();
        requestCountTV = new TextView(baseActivity);
        requestCountTV.setBackgroundColor(Color.GRAY);
        requestCountTV.setText("您有" + MyApp.requests.size() + "条好友请求");
        requestCountTV.setTextSize(20);
        listview.addHeaderView(requestCountTV);
        usernames = new ArrayList<>();
        adapter = new FriendsAdapter(MyApp.CONTEXT, usernames);
        listview.setBackgroundColor(Color.GREEN);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendsAdapter.ViewHolder vh = (FriendsAdapter.ViewHolder) view.getTag();
                Intent intent = new Intent(baseActivity, ChatActivity.class);
                intent.putExtra("toUser", vh.user);
                startActivity(intent);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                final String username = usernames.get(i - 1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().deleteContact(username);
                            usernames.remove(username);
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

                return false;
            }
        });
        requestCountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyApp.CONTEXT, RequestActivity.class);
                startActivity(i);
            }
        });
        loadFriends();
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String username) {
                loadFriends();
            }

            @Override
            public void onContactDeleted(String username) {
              loadFriends();
            }

            @Override
            public void onContactInvited(final String username, String reason) {
                Log.i("TAG", "onContactInvited: "+"接收到"+username+"的好友请求");
                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!MyApp.requests.contains(username)) {
                            MyApp.requests.add(username);
                        }
                        requestCountTV.setText("您有"+MyApp.requests.size()+"条好友请求");
                    }
                });
            }

            @Override
            public void onFriendRequestAccepted(String username) {
        loadFriends();
            }

            @Override
            public void onFriendRequestDeclined(String username) {

            }
        });

    }

    private void loadFriends() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    usernames = EMClient.getInstance().contactManager()
                            .getAllContactsFromServer();

                    baseActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(baseActivity, "好友数量：" + usernames.size(), Toast.LENGTH_SHORT)
                                    .show();

                            adapter = new FriendsAdapter(MyApp.CONTEXT,usernames);
                            listview.setAdapter(adapter);

                        }                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI(){

        requestCountTV.setText("您有"+MyApp.requests.size()+"条好友请求");
        adapter.notifyDataSetChanged();
    }
}
