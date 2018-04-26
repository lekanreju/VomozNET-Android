package com.vomozsystems.apps.android.vomoznet.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leksrej on 1/14/17.
 */

public class GetCountryStatesResponse {

    @SerializedName("xstatus")
    private String status;

    @SerializedName("xtrans_id")
    private String transId;

    @SerializedName("xfaultcode")
    private String faultCode;

    @SerializedName("xfaultstring")
    private String faultString;

    @SerializedName("xstates_provinces")
    private List<String> statesOrProvices;

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

    public List<String> getStatesOrProvices() {
        return statesOrProvices;
    }

    public void setStatesOrProvices(List<String> statesOrProvices) {
        this.statesOrProvices = statesOrProvices;
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
