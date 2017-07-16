package com.example.tarena.catchat.listener;

import com.example.tarena.catchat.bean.Blog;
import com.example.tarena.catchat.bean.MyUser;

/**
 * Created by tarena on 2017/7/10.
 */

public interface onCommentBlogListener {
    void onComment(int position, Blog blog);
    void onTapUser(MyUser user);
}
