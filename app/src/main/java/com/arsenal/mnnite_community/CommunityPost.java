package com.arsenal.mnnite_community;

import java.sql.Timestamp;

public class CommunityPost {

    public String user_id ,url, title, desc,time_stamp;

    public CommunityPost(){}

    public CommunityPost(String user_id, String url, String title, String desc, String time_stamp) {
        this.user_id = user_id;
        this.url = url;
        this.title = title;
        this.desc = desc;
        this.time_stamp = time_stamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
