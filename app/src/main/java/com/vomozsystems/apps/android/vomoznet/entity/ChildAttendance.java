package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 5/11/18.
 */

public class ChildAttendance {

    private Long id;
    private Long donationCenterCardId;
    private String merchantIdCode;
    private String childUniqueId;
    private Long parent1TexterCardId;
    private Long parent2TexterCardId;
    private Long checkInByTexterCardId;
    private String checkInByFullName;
    private Long checkOutByTexterCardId;
    private String checkOutByFullName;
    private String checkInDate;
    private String checkInTimestamp;
    private String checkOutDate;
    private String checkOutTimestamp;
    private String checkInSource;
    private String checkOutSource;
    private String password;
    private String callerId;

    public String getCallerId() {
        return callerId;
    }
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }
    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }
    public String getMerchantIdCode() {
        return merchantIdCode;
    }
    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }
    public String getChildUniqueId() {
        return childUniqueId;
    }
    public void setChildUniqueId(String childUniqueId) {
        this.childUniqueId = childUniqueId;
    }
    public Long getParent1TexterCardId() {
        return parent1TexterCardId;
    }
    public void setParent1TexterCardId(Long parent1TexterCardId) {
        this.parent1TexterCardId = parent1TexterCardId;
    }
    public Long getParent2TexterCardId() {
        return parent2TexterCardId;
    }
    public void setParent2TexterCardId(Long parent2TexterCardId) {
        this.parent2TexterCardId = parent2TexterCardId;
    }
    public Long getCheckInByTexterCardId() {
        return checkInByTexterCardId;
    }
    public void setCheckInByTexterCardId(Long checkInByTexterCardId) {
        this.checkInByTexterCardId = checkInByTexterCardId;
    }
    public String getCheckInByFullName() {
        return checkInByFullName;
    }
    public void setCheckInByFullName(String checkInByFullName) {
        this.checkInByFullName = checkInByFullName;
    }
    public Long getCheckOutByTexterCardId() {
        return checkOutByTexterCardId;
    }
    public void setCheckOutByTexterCardId(Long checkOutByTexterCardId) {
        this.checkOutByTexterCardId = checkOutByTexterCardId;
    }
    public String getCheckOutByFullName() {
        return checkOutByFullName;
    }
    public void setCheckOutByFullName(String checkOutByFullName) {
        this.checkOutByFullName = checkOutByFullName;
    }
    public String getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
    public String getCheckInTimestamp() {
        return checkInTimestamp;
    }
    public void setCheckInTimestamp(String checkInTimestamp) {
        this.checkInTimestamp = checkInTimestamp;
    }
    public String getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    public String getCheckOutTimestamp() {
        return checkOutTimestamp;
    }
    public void setCheckOutTimestamp(String checkOutTimestamp) {
        this.checkOutTimestamp = checkOutTimestamp;
    }
    public String getCheckInSource() {
        return checkInSource;
    }
    public void setCheckInSource(String checkInSource) {
        this.checkInSource = checkInSource;
    }
    public String getCheckOutSource() {
        return checkOutSource;
    }
    public void setCheckOutSource(String checkOutSource) {
        this.checkOutSource = checkOutSource;
    }
}
