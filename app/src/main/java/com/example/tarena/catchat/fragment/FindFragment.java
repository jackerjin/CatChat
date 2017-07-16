package com.example.tarena.catchat.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.tarena.catchat.R;
import com.example.tarena.catchat.adapter.PostAdapter;
import com.example.tarena.catchat.bean.Blog;
import com.example.tarena.catchat.bean.Comment;
import com.example.tarena.catchat.bean.MyUser;
import com.example.tarena.catchat.listener.onCommentBlogListener;
import com.example.tarena.catchat.ui.PostBlogActivity;
import com.example.tarena.catchat.ui.UserInfoActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends BaseFragment implements onCommentBlogListener {
    @BindView(R.id.findfragment_pullToRefreshlistview)
    PullToRefreshListView ptrListview;
    ListView listView;
    List<Blog> datas;
    PostAdapter adapter;
    @BindView(R.id.ll_find_commentcontainer)
    LinearLayout llCommentContainer;
    @BindView(R.id.et_find_comment)
    EditText etComment;
    Unbinder unbinder;
    private Blog blog;


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);


        return view;
    }

    @Override
    public void init() {
        // listView = ptrListview.getRefreshableView();
        datas = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), datas);
        adapter.setListener(this);
        ptrListview.setAdapter(adapter);

        ptrListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh();
            }

        });

    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        BmobQuery<Blog> query = new BmobQuery<>();
        query.include("author");
        query.order("-createdAt");
        query.findObjects(getActivity(), new FindListener<Blog>() {
            @Override
            public void onSuccess(List<Blog> list) {
                Log.i("TAG", "list:::::::" + list.toString());
                adapter.addAll(list, true);
                ptrListview.onRefreshComplete();
            }

            @Override
            public void onError(int i, String s) {
                baseActivity.toastAndLog("刷新帖子失败，请稍后重试", i, s);
                ptrListview.onRefreshComplete();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        baseActivity.toast("发朋友圈");
        baseActivity.jumpTo(PostBlogActivity.class, false, false);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComment(int position, Blog blog) {
        //判断如果已经显示则隐藏，如果隐藏则显示，用全局变量blog记录需要进行评论的blog
        if (llCommentContainer.getVisibility()== View.VISIBLE) {
            llCommentContainer.setVisibility(View.INVISIBLE);
        }else {
            llCommentContainer.setVisibility(View.VISIBLE);
            this.blog = blog;
        }

    }

    @Override
    public void onTapUser(MyUser user) {
        Intent intent = new Intent(baseActivity, UserInfoActivity.class);
        intent.putExtra("username", user.getUsername());
        startActivity(intent);


    }

    @OnClick(R.id.btn_find_send)
    public void onViewClicked(View view){
    //收起键盘
        InputMethodManager imm = (InputMethodManager) baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm!=null) {
            imm.hideSoftInputFromWindow(baseActivity.getWindow().getDecorView().getWindowToken(), 0);

        }
        Comment comment = new Comment();
        comment.setBlogId(blog.getObjectId());
        comment.setContent(etComment.getText().toString());
        comment.setUsername(BmobUser.getCurrentUser(baseActivity).getUsername());
        comment.save(getActivity(), new SaveListener() {
            @Override
            public void onSuccess() {
                baseActivity.toast("评论已发布");
                etComment.setText("");
                llCommentContainer.setVisibility(View.INVISIBLE);
                refresh();
            }

            @Override
            public void onFailure(int i, String s) {
                baseActivity.toastAndLog("评论发布失败，请稍后重试", i, s);
            }
        });
    }
}
