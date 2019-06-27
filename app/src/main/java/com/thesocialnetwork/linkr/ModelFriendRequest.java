package com.thesocialnetwork.linkr;

/**
 * Created by corei3 on 02-05-2018.
 */

public class ModelFriendRequest {

    String user_key;
    String user_name;
    String user_email;
    String user_status;

    public ModelFriendRequest() {
    }

    public ModelFriendRequest(String user_key,String user_name, String user_email, String user_status) {

        this.user_key=user_key;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_status = user_status;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

}
