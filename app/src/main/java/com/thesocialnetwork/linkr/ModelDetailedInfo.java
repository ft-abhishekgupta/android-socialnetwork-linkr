package com.thesocialnetwork.linkr;

/**
 * Created by corei3 on 29-04-2018.
 */

public class ModelDetailedInfo {

    String key;
    String data;
    String privacy;
    String type;

    public ModelDetailedInfo() {
    }

    public ModelDetailedInfo(String key, String data, String privacy, String type) {

        this.key = key;
        this.data = data;
        this.type = type;
        this.privacy = privacy;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

}
