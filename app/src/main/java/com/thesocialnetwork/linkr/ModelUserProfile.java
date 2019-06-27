package com.thesocialnetwork.linkr;

/**
 * Created by corei3 on 11-04-2018.
 */

public class ModelUserProfile {

    String profile_uid;
    String profile_name;
    String profile_phone;
    String profile_email;
    String profile_img;
    String profile_online_status;

    public String getProfile_uid() {
        return profile_uid;
    }

    public void setProfile_uid(String profile_uid) {
        this.profile_uid = profile_uid;
    }

    public ModelUserProfile() {
    }

    public String getProfile_online_status() {
        return profile_online_status;
    }

    public void setProfile_online_status(String profile_online_status) {
        this.profile_online_status = profile_online_status;
    }

    public ModelUserProfile(String profile_uid, String profile_name, String profile_phone, String profile_email, String profile_img, String profile_online_status) {

        this.profile_uid=profile_uid;
        this.profile_name = profile_name;
        this.profile_phone = profile_phone;
        this.profile_email = profile_email;
        this.profile_img = profile_img;
        this.profile_online_status=profile_online_status;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_phone() {
        return profile_phone;
    }

    public void setProfile_phone(String profile_phone) {
        this.profile_phone = profile_phone;
    }

    public String getProfile_email() {
        return profile_email;
    }

    public void setProfile_email(String profile_email) {
        this.profile_email = profile_email;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
