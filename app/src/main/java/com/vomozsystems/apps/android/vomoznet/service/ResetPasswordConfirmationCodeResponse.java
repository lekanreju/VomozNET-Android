package com.vomozsystems.apps.android.vomoznet.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leksrej on 9/5/16.
 */
public class ResetPasswordConfirmationCodeResponse {
    @SerializedName("xstatus")
    private String status;

    @SerializedName("xtrans_id")
    private String transId;

    @SerializedName("xresult_type")
    private String result;

    @SerializedName("xfaultcode")
    private String faultCode;

    @SerializedName("xfaultstring")
    private String faultString;

    @SerializedName("xmessage")
    private String message;

    @SerializedName("xidentification_code")
    private String identificationCode;

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
