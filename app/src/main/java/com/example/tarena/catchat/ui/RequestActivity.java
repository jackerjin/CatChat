package com.example.tarena.catchat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tarena.catchat.app.MyApp;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by tarena on 2017/7/12.
 */

public class RequestActivity extends AppCompatActivity {
    ListView listview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listview = new ListView(this);
        setContentView(listview);
        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                MyApp.requests
        ));
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {


                //同意
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String username = MyApp.requests.get(i);
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(username);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RequestActivity.this, "同意发送完成", Toast
                                            .LENGTH_SHORT)
                                            .show();

                                    MyApp.requests.remove(username);
                                    ArrayAdapter adapter = (ArrayAdapter) listview.getAdapter();
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
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                //拒绝
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String username = MyApp.requests.get(i);
                        try {
                            EMClient.getInstance().contactManager().declineInvitation(username);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RequestActivity.this, "拒绝发送完成", Toast
                                            .LENGTH_SHORT)
                                            .show();

                                    MyApp.requests.remove(username);
                                    ArrayAdapter adapter = (ArrayAdapter) listview.getAdapter();
                                    adapter.notifyDataSetChanged();


                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            }
        });

    }




}
