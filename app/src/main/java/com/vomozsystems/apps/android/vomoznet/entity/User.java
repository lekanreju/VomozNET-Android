package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    private Long userId;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String mobilePhone;
    private String texterCardId;
    private Double currentYearTotalContribution;
    private String envelopeId;
    private String authToken;
    private String profileImage;
    private RealmList<DonationCenter> donationCenters;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(String texterCardId) {
        this.texterCardId = texterCardId;
    }

    public Double getCurrentYearTotalContribution() {
        return currentYearTotalContribution;
    }

    public void setCurrentYearTotalContribution(Double currentYearTotalContribution) {
        this.currentYearTotalContribution = currentYearTotalContribution;
    }

    public String getEnvelopeId() {
        return envelopeId;
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public RealmList<DonationCenter> getDonationCenters() {
        return donationCenters;
    }

    public void setDonationCenters(RealmList<DonationCenter> donationCenters) {
        this.donationCenters = donationCenters;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}



