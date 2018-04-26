package com.vomozsystems.apps.android.vomoznet.service;

public class RegistrationRequest {

    private String appOs;
    private String registrationToken;
    private String callerId;
    private Long centerCardId;
    private String deviceId;

    /**
     * @return the appOs
     */
    public String getAppOs() {
        return appOs;
    }

    /**
     * @param appOs the appOs to set
     */
    public void setAppOs(String appOs) {
        this.appOs = appOs;
    }

    /**
     * @return the registrationToken
     */
    public String getRegistrationToken() {
        return registrationToken;
    }

    /**
     * @param registrationToken the registrationToken to set
     */
    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    /**
     * @return the callerId
     */
    public String getCallerId() {
        return callerId;
    }

    /**
     * @param callerId the callerId to set
     */
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
