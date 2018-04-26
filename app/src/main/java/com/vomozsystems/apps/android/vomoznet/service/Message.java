package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 6/18/16.
 */
public class Message {
    private Integer code;
    private String type;
    private String description;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
