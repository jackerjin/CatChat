package com.example.tarena.catchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/7/10.
 */

public class Comment extends BmobObject {

    String username;
    String content;
    String blogId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
}
