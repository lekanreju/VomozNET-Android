package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 5/8/18.
 */

import java.util.Date;

public class Child {

    private Long id;
    private Long donationCenterCardId;
    private String merchantIdCode;
    private String childUniqueId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private String ageGroup;
    private String birthDate;
    private String suffix;
    private String gender;
    private String grade;
    private String schoolName;
    private String pic1;
    private String pic2;
    private String pic3;
    private String pic4;
    private Long parent1TexterCardId;
    private Long parent2TexterCardId;
    private String callerId;
    private String password;
    private String checkInStatus;
    public String getCheckInStatus() {
        return checkInStatus;
    }
    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }
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
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
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
    public String getPreferredName() {
        return preferredName;
    }
    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }
    public String getAgeGroup() {
        return ageGroup;
    }
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getSuffix() {
        return suffix;
    }
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGrade() {
        return grade;
    }
    public void setGrade(String grade) {
        this.grade = grade;
    }
    public String getSchoolName() {
        return schoolName;
    }
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
    public String getPic1() {
        return pic1;
    }
    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }
    public String getPic2() {
        return pic2;
    }
    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }
    public String getPic3() {
        return pic3;
    }
    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }
    public String getPic4() {
        return pic4;
    }
    public void setPic4(String pic4) {
        this.pic4 = pic4;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
