package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leksrej on 6/28/17.
 */

public class Config extends RealmObject {
    @PrimaryKey
    private String configId;
    private String email;
    private String mobilePhone;
    private String password;
    private String lastPage;
    private Boolean loggedIn;
    private String accessCode;
    private String resetPasswordCode;
    private Integer failedAttemptCount = 0;
    private Integer sendResetEmailCount = 0;
    private Integer sendAccessCodeCount = 0;
    private Long currentDonationCenterCardId;
    private Integer securityQuestion1Id;
    private Integer securityQuestion2Id;
    private String securityQuestion1Answer;
    private String securityQuestion1Text;
    private String securityQuestion2Text;
    private String securityQuestion2Answer;
    private String verifiedPhoneNumbers;
    private String firstName;
    private String lastName;
    private Long lastCreditCardIdUsed;
    private String cardOrBank;

    public String getFirstName() {
        if(firstName == null)
            return "Unknown";
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        if(lastName == null)
            return "Unknown";
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getVerifiedPhoneNumbers() {
        return verifiedPhoneNumbers;
    }

    public void setVerifiedPhoneNumbers(String verifiedPhoneNumbers) {
        this.verifiedPhoneNumbers = verifiedPhoneNumbers;
    }

    public Integer getSecurityQuestion1Id() {
        return securityQuestion1Id;
    }

    public void setSecurityQuestion1Id(Integer securityQuestion1Id) {
        this.securityQuestion1Id = securityQuestion1Id;
    }

    public Integer getSecurityQuestion2Id() {
        return securityQuestion2Id;
    }

    public void setSecurityQuestion2Id(Integer securityQuestion2Id) {
        this.securityQuestion2Id = securityQuestion2Id;
    }

    public String getSecurityQuestion1Answer() {
        return securityQuestion1Answer;
    }

    public void setSecurityQuestion1Answer(String securityQuestion1Answer) {
        this.securityQuestion1Answer = securityQuestion1Answer;
    }

    public String getSecurityQuestion2Answer() {
        return securityQuestion2Answer;
    }

    public void setSecurityQuestion2Answer(String securityQuestion2Answer) {
        this.securityQuestion2Answer = securityQuestion2Answer;
    }

    public Long getCurrentDonationCenterCardId() {
        return currentDonationCenterCardId;
    }

    public void setCurrentDonationCenterCardId(Long currentDonationCenterCardId) {
        this.currentDonationCenterCardId = currentDonationCenterCardId;
    }

    public Integer getSendResetEmailCount() {
        return sendResetEmailCount;
    }

    public void setSendResetEmailCount(Integer sendResetEmailCount) {
        this.sendResetEmailCount = sendResetEmailCount;
    }

    public Integer getSendAccessCodeCount() {
        return sendAccessCodeCount;
    }

    public void setSendAccessCodeCount(Integer sendAccessCodeCount) {
        this.sendAccessCodeCount = sendAccessCodeCount;
    }

    public String getResetPasswordCode() {
        return resetPasswordCode;
    }

    public void setResetPasswordCode(String resetPasswordCode) {
        this.resetPasswordCode = resetPasswordCode;
    }

    public Integer getFailedAttemptCount() {
        return failedAttemptCount;
    }

    public void setFailedAttemptCount(Integer failedAttemptCount) {
        this.failedAttemptCount = failedAttemptCount;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastPage() {
        return lastPage;
    }

    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }

    public String getSecurityQuestion1Text() {
        return securityQuestion1Text;
    }

    public void setSecurityQuestion1Text(String securityQuestion1Text) {
        this.securityQuestion1Text = securityQuestion1Text;
    }

    public String getSecurityQuestion2Text() {
        return securityQuestion2Text;
    }

    public void setSecurityQuestion2Text(String securityQuestion2Text) {
        this.securityQuestion2Text = securityQuestion2Text;
    }
}
