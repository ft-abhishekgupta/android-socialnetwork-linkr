package com.thesocialnetwork.linkr;

/**
 * Created by corei3 on 28-05-2018.
 */

public class ModelMessage {

    String text;
    String time;
    String sent_by;

    public ModelMessage() {
    }

    public ModelMessage(String text, String time, String sent_by) {
        this.text = text;
        this.time = time;
        this.sent_by = sent_by;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSent_by() {
        return sent_by;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }
}
