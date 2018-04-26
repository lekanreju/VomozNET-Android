package com.vomozsystems.apps.android.vomoznet.entity;

import java.util.Date;

public class Personal {
    private Long personalId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String primaryEmail;
    private String secondaryEmail;
    private String mobilePhone;
    private String homePhone;
    private String additionalPhone;
    private String maritalStatus;
    private String gender;
    private String envelopeId;
    private String suffix;
    private Date birthDate;
    private Date firstVisitDate;
    private String title;
    private String authPass;
    private Date weddingDate;
    private Date memberSince;
    private Date baptismDate;
    private Long centerCardId;
    private Long texterCardId;
    private String donationCenterName;
    private String donationCenterLogo;
    private String position;
    private String department;
    private String showProfilePicture;
    private String showEmail;
    private String showPhone;
    private String showFullName;
    private String recordListing;
    private String showDepartment;
    private String showDepartmentPosition;
    private String profilePicture;

    public Long getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }


    public String getRecordListing() {
        return recordListing;
    }

    public void setRecordListing(String recordListing) {
        this.recordListing = recordListing;
    }

    public String getShowProfilePicture() {
        return showProfilePicture;
    }

    public void setShowProfilePicture(String showProfilePicture) {
        this.showProfilePicture = showProfilePicture;
    }

    public String getShowEmail() {
        return showEmail;
    }

    public void setShowEmail(String showEmail) {
        this.showEmail = showEmail;
    }

    public String getShowPhone() {
        return showPhone;
    }

    public void setShowPhone(String showPhone) {
        this.showPhone = showPhone;
    }

    public String getShowFullName() {
        return showFullName;
    }

    public void setShowFullName(String showFullName) {
        this.showFullName = showFullName;
    }

    public String getShowDepartment() {
        return showDepartment;
    }

    public void setShowDepartment(String showDepartment) {
        this.showDepartment = showDepartment;
    }

    public String getShowDepartmentPosition() {
        return showDepartmentPosition;
    }

    public void setShowDepartmentPosition(String showDepartmentPosition) {
        this.showDepartmentPosition = showDepartmentPosition;
    }

    public String getDonationCenterName() {
        return donationCenterName;
    }

    public void setDonationCenterName(String donationCenterName) {
        this.donationCenterName = donationCenterName;
    }

    public String getDonationCenterLogo() {
        return donationCenterLogo;
    }

    public void setDonationCenterLogo(String donationCenterLogo) {
        this.donationCenterLogo = donationCenterLogo;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        if (null != middleName && !middleName.equalsIgnoreCase("0"))
            return middleName;
        else
            return "";
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return the primaryEmail
     */
    public String getPrimaryEmail() {
        if (null != primaryEmail && !primaryEmail.equalsIgnoreCase("0"))
            return primaryEmail;
        else
            return "";
    }

    /**
     * @param primaryEmail the primaryEmail to set
     */
    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    /**
     * @return the secondaryEmail
     */
    public String getSecondaryEmail() {
        if (null != secondaryEmail && !secondaryEmail.equalsIgnoreCase("0"))
            return secondaryEmail;
        else
            return "";
    }

    /**
     * @param secondaryEmail the secondaryEmail to set
     */
    public void setSecondaryEmail(String secondaryEmail) {
        this.secondaryEmail = secondaryEmail;
    }

    /**
     * @return the mobilePhone
     */
    public String getMobilePhone() {
        if (null != mobilePhone && !mobilePhone.equalsIgnoreCase("0"))
            return mobilePhone;
        else
            return "";
    }

    /**
     * @param mobilePhone the mobilePhone to set
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @return the homePhome
     */
    public String getHomePhone() {
        if (null != homePhone && !homePhone.equalsIgnoreCase("0"))
            return homePhone;
        else
            return "";
    }

    /**
     * @param homePhone the homePhone to set
     */
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    /**
     * @return the additionalPhone
     */
    public String getAdditionalPhone() {
        if (null != additionalPhone && !additionalPhone.equalsIgnoreCase("0"))
            return additionalPhone;
        else
            return "";
    }

    /**
     * @param additionalPhone the additionalPhone to set
     */
    public void setAdditionalPhone(String additionalPhone) {
        this.additionalPhone = additionalPhone;
    }

    /**
     * @return the maritalStatus
     */
    public String getMaritalStatus() {
        if (null != maritalStatus && !maritalStatus.equalsIgnoreCase("0"))
            return maritalStatus;
        else
            return "";
    }

    /**
     * @param maritalStatus the maritalStatus to set
     */
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        if (null != gender && !gender.equalsIgnoreCase("0"))
            return gender;
        else
            return "";
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the birthDate
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the birthDate to set
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        if (null != title && !title.equalsIgnoreCase("0"))
            return title;
        else
            return "";
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the authPass
     */
    public String getAuthPass() {
        return authPass;
    }

    /**
     * @param authPass the authPass to set
     */
    public void setAuthPass(String authPass) {
        this.authPass = authPass;
    }

    /**
     * @return the weddingDate
     */
    public Date getWeddingDate() {
        return weddingDate;
    }

    /**
     * @param weddingDate the weddingDate to set
     */
    public void setWeddingDate(Date weddingDate) {
        this.weddingDate = weddingDate;
    }

    /**
     * @return the memberSince
     */
    public Date getMemberSince() {
        return memberSince;
    }

    /**
     * @param memberSince the memberSince to set
     */
    public void setMemberSince(Date memberSince) {
        this.memberSince = memberSince;
    }

    /**
     * @return the baptismDate
     */
    public Date getBaptismDate() {
        return baptismDate;
    }

    /**
     * @param baptismDate the baptismDate to set
     */
    public void setBaptismDate(Date baptismDate) {
        this.baptismDate = baptismDate;
    }

    /**
     * @return the firstVisitDate
     */
    public Date getFirstVisitDate() {
        return firstVisitDate;
    }

    /**
     * @param firstVisitDate the firstVisitDate to set
     */
    public void setFirstVisitDate(Date firstVisitDate) {
        this.firstVisitDate = firstVisitDate;
    }

    public String getEnvelopeId() {
        if (null != envelopeId && !envelopeId.equalsIgnoreCase("0"))
            return envelopeId;
        else
            return "";
    }

    public void setEnvelopeId(String envelopeId) {
        this.envelopeId = envelopeId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }
}