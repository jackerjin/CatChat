package com.example.tarena.catchat.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tarena.catchat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by tarena on 2017/7/7.
 */


public abstract class MyBaseAdapter<T> extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<T> datasource;

    public MyBaseAdapter(Context context, List<T> datasource) {
        super();
        this.context = context;
        this.datasource = datasource;
//		this.inflater = LayoutInflater.from(context);



        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datasource.size();
    }

    @Override
    public T getItem(int position) {
        return datasource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(List<T> list, boolean isClear){
        if(isClear){
            datasource.clear();
        }
        datasource.addAll(list);
        notifyDataSetChanged();
    }

    public void add(T t){
        datasource.add(t);
        notifyDataSetChanged();
    }

    public void remove(T t){
        datasource.remove(t);
        notifyDataSetChanged();
    }

    public void clear(){
        datasource.clear();
        notifyDataSetChanged();
    }

    public void setAvatar(String url, ImageView iv){
        if(TextUtils.isEmpty(url)){
            iv.setImageResource(R.drawable.ic_launcher);
        }else{
            ImageLoader.getInstance().displayImage(url, iv);
        }
    }
}
