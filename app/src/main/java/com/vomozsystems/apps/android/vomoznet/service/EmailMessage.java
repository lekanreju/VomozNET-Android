package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 4/30/18.
 */

public class EmailMessage {
    private String subject;
    private String body;
    private String receiver;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
