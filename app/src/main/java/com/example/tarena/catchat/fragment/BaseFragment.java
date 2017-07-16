package com.example.tarena.catchat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tarena.catchat.ui.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by tarena on 2017/7/5.
 */

public abstract class BaseFragment extends Fragment {
   BaseActivity baseActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createMyView(inflater, container, savedInstanceState);
        ButterKnife.bind(this,view);
        baseActivity= (BaseActivity) getActivity();
        //如果需要在fragment页面的ActionBar里面显示按钮的话需要调用下面代码
        setHasOptionsMenu(true);
        init();
        return view;
    }

    public void init(){

    }

    public abstract View createMyView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState);
}
