package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 7/5/17.
 */

public class VomozGlobalInfo {

    private Long id;
    private String callerId;
    private Long texterCardId;
    private Long donationCenterCardId;
    private String emailAddress;
    private String password;
    private String firstName;
    private String lastName;
    private String googleCloudToken;
    private Integer resetPasswordQuestion1;
    private Integer resetPasswordQuestion2;
    private Integer resetPasswordQuestion3;
    private String resetPasswordAnswer1;
    private String resetPasswordAnswer2;
    private String resetPasswordAnswer3;
    private boolean updateVzNet;

    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }
    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }
    public Long getTexterCardId() {
        return texterCardId;
    }
    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }
    public boolean isUpdateVzNet() {
        return updateVzNet;
    }
    public void setUpdateVzNet(boolean updateVzNet) {
        this.updateVzNet = updateVzNet;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCallerId() {
        return callerId;
    }
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getGoogleCloudToken() {
        return googleCloudToken;
    }
    public void setGoogleCloudToken(String googleCloudToken) {
        this.googleCloudToken = googleCloudToken;
    }
    public Integer getResetPasswordQuestion1() {
        return resetPasswordQuestion1;
    }
    public void setResetPasswordQuestion1(Integer resetPasswordQuestion1) {
        this.resetPasswordQuestion1 = resetPasswordQuestion1;
    }
    public Integer getResetPasswordQuestion2() {
        return resetPasswordQuestion2;
    }
    public void setResetPasswordQuestion2(Integer resetPasswordQuestion2) {
        this.resetPasswordQuestion2 = resetPasswordQuestion2;
    }
    public Integer getResetPasswordQuestion3() {
        return resetPasswordQuestion3;
    }
    public void setResetPasswordQuestion3(Integer resetPasswordQuestion3) {
        this.resetPasswordQuestion3 = resetPasswordQuestion3;
    }
    public String getResetPasswordAnswer1() {
        return resetPasswordAnswer1;
    }
    public void setResetPasswordAnswer1(String resetPasswordAnswer1) {
        this.resetPasswordAnswer1 = resetPasswordAnswer1;
    }
    public String getResetPasswordAnswer2() {
        return resetPasswordAnswer2;
    }
    public void setResetPasswordAnswer2(String resetPasswordAnswer2) {
        this.resetPasswordAnswer2 = resetPasswordAnswer2;
    }
    public String getResetPasswordAnswer3() {
        return resetPasswordAnswer3;
    }
    public void setResetPasswordAnswer3(String resetPasswordAnswer3) {
        this.resetPasswordAnswer3 = resetPasswordAnswer3;
    }
}
