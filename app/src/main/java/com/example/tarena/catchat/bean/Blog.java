package com.example.tarena.catchat.bean;

import com.example.tarena.catchat.bean.MyUser;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by tarena on 2017/7/6.
 */

public class Blog extends BmobObject {

    MyUser author; //blog作者
    String content;//blog的正文
    String imgUrls;//blog的所有配图地址
    ArrayList<String> loveUsers;



    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }

    public ArrayList<String> getLoveUsers() {
        if (loveUsers == null) {
            loveUsers = new ArrayList<>();

        }
        return loveUsers;
    }

    public void setLoveUsers(ArrayList<String> loveUsers) {
        this.loveUsers = loveUsers;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "author=" + author +
                ", content='" + content + '\'' +
                ", imgUrls='" + imgUrls + '\'' +
                ", loveUsers=" + loveUsers +
                '}';
    }
}
