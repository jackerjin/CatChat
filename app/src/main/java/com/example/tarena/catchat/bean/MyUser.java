package com.example.tarena.catchat.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by tarena on 2017/7/5.
 */

public class MyUser extends BmobUser {
    //性别，位置，拼音名称和拼音首字母
    Boolean gender;//true为男生，false女生
    String avatar;
    String nick;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

}
