package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 6/18/16.
 */
public class BaseServiceResponse {

    public static final int SUCCESS_CODE = 100;

    private Long transactionId;
    private String service;
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
