package com.vomozsystems.apps.android.vomoznet.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leksrej on 8/17/16.
 */
public class MakeDonationResponse {
    @SerializedName("xstatus")
    private String status;

    @SerializedName("xtrans_id")
    private String transId;

    @SerializedName("xresult")
    private String result;

    @SerializedName("xfaultcode")
    private String faultCode;

    @SerializedName("xfaultstring")
    private String faultString;

    @SerializedName("xother_information")
    private Information information;

    public Information getInformation() {
        return information;
    }

    public void setInformation(Information information) {
        this.information = information;
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
}

